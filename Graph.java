import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import java.util.AbstractMap;
// import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Graph extends JPanel {
    private static final int PADDING = 60; // margins
    private int yAxisMin = -5; // going to change this so it is dynamic, where the min is rounded to the next lowest 5 and max is rounded to next highest five, so the spread of lines is greatest no matter what
    private int yAxisMax = 30;
    private final int HOVER_THRESHOLD = 10;
    private String differenceLabel; // this is the varable that is changed to indicate difference
    
    private PointValue firstClickedValue = null; // couldve used a set, so more than two could be selected, but idk why youd want more than 2
    private PointValue secondClickedValue = null;
    private Point hoverPoint = null; // default null cuz nothing is displayed yet
    private String hoverText = null;
    
    private boolean showExpected = false; // flag for overlay

    private final List<PointValue> dataPoints = new ArrayList<>(); // a list of each positon to be made ong arph, 
    
    private final List<Employee> activeEmployees = new ArrayList<>(); // luist of active employeees to go on the list in main

    public Graph() {   // construcot
        setPreferredSize(new Dimension(800, 800)); // makes the window, subject to change idk what custoemr wants, but i shall make it "stretched" vertically so line graph is more apparent
        addMouseMotionListener(new MouseAdapter() { // lsitenr for hovering mouse
            @Override
            public void mouseMoved(MouseEvent e) {
                checkHover(e.getX(), e.getY()); // update the state if we need to, if the mouse is withing the pixel box we draw, checkHover will dispaly
                repaint(); // and update the graoph, but weird ass name
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                captureClickedPoint(e.getX(), e.getY());
                repaint(); // Optionally repaint if you want to show the selection
            }
        });
    }

    public void setShowExpected(boolean showExpected) { // can be accesed by main so overlay of expected can be printed
        this.showExpected = showExpected;
        repaint();
    }

    public List<Employee> getActiveEmployees() {
        return new ArrayList<>(activeEmployees); // for main
    }
    
    public void toggleEmployee(Employee employee) { // pretty straight forward, altering the list basically if we choose from drop down
        if (activeEmployees.contains(employee)) {
            activeEmployees.remove(employee);
        } else {
            activeEmployees.add(employee);
        }
        differenceLabel = null;
        firstClickedValue = null;
        secondClickedValue = null;
        repaint(); // change the graph again
    }
    
    public void setEmployees(List<Employee> employees) { // refresh and start over, hence the clear, add all, and repaint, so display all
        activeEmployees.clear();
        activeEmployees.addAll(employees);
        differenceLabel = null;
        repaint();
    }

    // three cases that we want to calculate.
    // If two points are clicked in same emplyee, cal percent difference
    // if two points are clicked on same employee, but one is expected and one is normal and are the same month, calculate how far off they
    // if two points are clicked on different employee but on the same month, calculate how much better one performed then the other\
    private int monthToNumber(String month) { // for comparing down below
        switch (month.toLowerCase()) {
            case "january":   return 1;
            case "february":  return 2;
            case "march":     return 3;
            case "april":     return 4;
            case "may":       return 5;
            case "june":      return 6;
            case "july":      return 7;
            case "august":    return 8;
            case "september": return 9;
            case "october":   return 10;
            case "november":  return 11;
            case "december":  return 12;
            default:          return 0;
        }
    }

    private void calDifference(){ 
        String label = differenceLabel;
        if(firstClickedValue == null || secondClickedValue == null ||  firstClickedValue.value == secondClickedValue.value){ // if one or more points are not selected
            label = null;
            return;
        }  
        PointValue earlier, later;
        int month1 = monthToNumber(firstClickedValue.month); // 5
        int month2 = monthToNumber(firstClickedValue.month); //4
        if(month1  <= month2){
            earlier = firstClickedValue;
            later = secondClickedValue;
        } else {
            earlier = secondClickedValue;
            later = firstClickedValue;
        }

        double percentDifference = ((later.value - earlier.value) / earlier.value) * 100;
        if (firstClickedValue.employee == secondClickedValue.employee && firstClickedValue.expected == false && secondClickedValue.expected == false){ // if two points of the same employee are selected and there both not expected values
            label = "Difference of: " + String.format("%.2f", percentDifference) + "%";
        } else if(firstClickedValue.employee == secondClickedValue.employee && firstClickedValue.month.equals(secondClickedValue.month) && ((firstClickedValue.expected == false && secondClickedValue.expected == true) || (firstClickedValue.expected == true && secondClickedValue.expected == false))){ // same employee, same month, one expected one not expected
            percentDifference = ((Math.max(firstClickedValue.value, secondClickedValue.value) - Math.min(firstClickedValue.value, secondClickedValue.value)) / ((firstClickedValue.value + secondClickedValue.value) / 2)) * 100;
            if(firstClickedValue.value > secondClickedValue.value && firstClickedValue.expected == true){
                label = firstClickedValue.employee.getName().split(",")[0] + " Performed -" + String.format("%.2f", percentDifference) + "% under expected "  + firstClickedValue.month;
            } else if(firstClickedValue.value < secondClickedValue.value && firstClickedValue.expected == false){
                label = firstClickedValue.employee.getName().split(",")[0] + " Performed -" + String.format("%.2f", percentDifference) + "% under expected "  + firstClickedValue.month;
            } else {
                label = firstClickedValue.employee.getName().split(",")[0] + " Performed " + String.format("%.2f", percentDifference) + "% over expected in " + firstClickedValue.month;
            }
        } else {
            label = "Bad Selection";
        }
        firstClickedValue = null;
        secondClickedValue = null;
        differenceLabel = label;
    }

    public void clearEmployees() { // clear all 
        activeEmployees.clear();
        differenceLabel = null;
        secondClickedValue = null;
        firstClickedValue = null;
        repaint();
    }
    
    public boolean hasAll(List<Employee> allEmployees) {
        return activeEmployees.containsAll(allEmployees) && (activeEmployees.size() == allEmployees.size()); // check in from enitre list whos active
    }
    
    public void setYAxisRange(int min, int max) { // for chaning when thebutton is pressed, will be change to "checkRange" that is called when any button that alters activeEmployees so this can check if the range is appropirate to display everything
        if (min >= max) {
            throw new IllegalArgumentException("Y-axis min must be less than max"); // this is what is caught by main
        }
        this.yAxisMin = min;
        this.yAxisMax = max;
        repaint();
    }
    
    private void checkHover(int mouseX, int mouseY) {
        // final int HOVER_THRESHOLD = 5; // how clsoe we consider it to be, in pixels, to display data
        hoverPoint = null;
        hoverText = null;
        for (PointValue pv : dataPoints) { // if it is in the dataPoints, then we display the corresponding employee stuff
            if (Math.abs(pv.point.x - mouseX) <= HOVER_THRESHOLD && Math.abs(pv.point.y - mouseY) <= HOVER_THRESHOLD) { // uses absolute value to check if BOTH x and y are within the threshold
                hoverPoint = new Point(mouseX, mouseY); // if they are, change the variable to the current point
                hoverText = String.format("%s: %.1f", pv.employee.getName().chars().mapToObj(c -> String.valueOf((char) c)).takeWhile(c -> !c.equals(",")).reduce("", String::concat), pv.value); // and display, googled inline maping so i onyl get the last name, chat helped here obv
                break; // stop when u find one, can only be one cuz we only want to display one
            }
        }
    }

    private void captureClickedPoint(int mouseX, int mouseY){
        // firstClickedValue = null;
        PointValue clicked = null;
        for(PointValue pv : dataPoints){
            if(Math.abs(pv.point.x - mouseX) <= HOVER_THRESHOLD && Math.abs(pv.point.y - mouseY) <= HOVER_THRESHOLD){
                clicked = pv;
                System.out.println("Clicked on: " + pv.employee.getName() + " with value: " + pv.value);
                break;
            }
        }
        if(firstClickedValue != null && secondClickedValue != null){
            firstClickedValue = clicked;
            secondClickedValue = null;
        } else if (firstClickedValue != null && secondClickedValue == null){
            secondClickedValue = clicked;
        } else if (firstClickedValue == null && secondClickedValue == null){
            firstClickedValue = clicked;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dataPoints.clear();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawAxes(g2);

        // draw actual performance data for each employee
        for (Employee emp : activeEmployees) {
            drawEmployeeData(g2, emp);
        }
        
        // if flag overlay the expected performance stuff
        if (showExpected) {
            displayExpected(g2, true);
        }

        calDifference(); // changes the variable for below to alter
        drawHoverTooltip(g2);
        highLightPoint(g2);

        if (differenceLabel != null && !differenceLabel.isEmpty()) {
            g2.setColor(Color.BLACK);
            g2.drawString(differenceLabel, (((getWidth() - 2 * PADDING)) / 2) - 40, PADDING - 20); // not sure why this isnt in the middle
        } 
    }

    private void highLightPoint(Graphics2D g2){
        g2.setColor(new Color(255, 255, 0));
        if(firstClickedValue != null){
            g2.fillOval(firstClickedValue.point.x - 3, firstClickedValue.point.y - 3, 6, 6);
        }
        if(secondClickedValue != null){
            g2.fillOval(secondClickedValue.point.x - 3, secondClickedValue.point.y - 3, 6, 6);
        }
        if(hoverPoint != null && hoverText != null){ // same as below, if we are hovering, then we draw it on what we are hoving at
            for(PointValue pv : dataPoints){
                if (Math.abs(pv.point.x - hoverPoint.x) <= HOVER_THRESHOLD && Math.abs(pv.point.y - hoverPoint.y) <= HOVER_THRESHOLD) { // exact match
                g2.fillOval(pv.point.x - 3, pv.point.y - 3, 6, 6);

                // g2.drawLine(pv.point.x - 3, pv.point.y - 10, pv.point.x - 3, pv.point.y - 5);
                break;
                }   
            }
            
        }
    }
            // g2.fillOval(hoverPoint.x, hoverPoint.y, 6, 6);
    

    private void drawHoverTooltip(Graphics2D g2) {
        if (hoverPoint != null && hoverText != null) { // fi we are hovering
            int x = hoverPoint.x + 10; // rectanlge size for text
            int y = hoverPoint.y - 10;
            g2.setColor(new Color(255, 255, 225)); // off white ish
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(hoverText) + 10; // get width of the rect text
            int textAscent = fm.getAscent(); // gets the distance between bottom of lowest character and top of highest character
            int textHeight = fm.getHeight(); // get height of rect text
            g2.fillRect(x, y - textHeight, textWidth, textHeight); // draws the rectangle
            g2.setColor(Color.BLACK); 
            g2.drawRect(x, y - textHeight, textWidth, textHeight);
            g2.drawString(hoverText, x + 5, y - textHeight + (textHeight + textAscent) / 2); // hover barely to the right and up, so its minus the hieght of the box + (the height of the box and hieght of min max characters)/ 2
        }
    }
    
    private void drawAxes(Graphics2D g2) {
        g2.setColor(Color.BLACK); // drawn in black
    
        g2.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING); // starts at top left after margin, which is my padding, and ends bottom left minus padding again  
    
        g2.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING); // same thing but oppsite
        
        // g2.drawLine(PADDING, getHeight() / 2, getWidth() - PADDING, getHeight() / 2 ); // draws a lie in the middle, idk why i had it as ifstatement cuz if range changes it wont run
        
        boolean hasRun = false;

        double curMax = Double.NEGATIVE_INFINITY;
        double curMin = Double.POSITIVE_INFINITY;
        
        for (Employee emp : activeEmployees) {
            Map<String, Double> monthlyData = emp.getMonthlyData();
            double min = Collections.min(monthlyData.values());
            double max = Collections.max(monthlyData.values());
            if(showExpected){
                double expected = emp.getExpected();
                if(expected > max){
                    max = expected;
                }
                if(expected < min){
                    min = expected;
                }
            }  // if show expected is false, we do not consider it in the calculation of range
            if(curMax < max){
                curMax = max;
            }
            if(curMin < min){
                curMin = min;
            }
        }
        //        for (Double values : emp.getMonthlyData().entrySet().stream().collect(Collectors.toList())) {
            //     if (showExpected) { // so the range changes on if show expected is toggled
            //         // Process both indices in the array.
            //         for (Double value : values) {
            //             if (value != null) {
            //                 if (value > curMax) {
            //                     curMax = value;
            //                 }
            //                 // ignore -100.05 for the min calculation.
            //                 if (value < curMin && value != -100.05) {
            //                     curMin = value;
            //                 }
            //             }
            //         }
            //     } else {
            //         //  process the first index (actual value) of the array.
            //         Double value = values[0];
            //         if (value != null) {
            //             if (value > curMax) {
            //                 curMax = value;
            //             }
            //             if (value < curMin && value != -100.05) {
            //                 curMin = value;
            //             }
            //         }
            //     }
            // }
        if(activeEmployees.isEmpty() == false) { // handling for dynamic range now
                if( (int) (Math.ceil(curMax / 5.0) * 5) <= 0){
                    yAxisMax = 5;
                } else {
                    yAxisMax = (int) (Math.ceil(curMax / 5.0) * 5);
                }
                if ( (int) (Math.floor(curMin / 5.0) * 5) >= 0){
                    yAxisMin = -5;
                } else {
                    yAxisMin = (int) (Math.floor(curMin / 5.0) * 5);
                }
            }
        //     } else {
        //     yAxisMax = 10;
        //     yAxisMin = -10;
        // }
        for (int y = yAxisMin; y <= yAxisMax; y += 5) { // loop over the axis in 5 increments, maybe this is gonna change idk
            int yPos = mapY(y); // find pixel postion, play around with this until it lines up, play aroudn with above yAxisMin start point too
            if(0 > yAxisMin && 0 < yAxisMax){
                int zeroLoc = mapY(0);
                g2.drawLine(PADDING, zeroLoc, getWidth() - PADDING, zeroLoc); // draws a line where zero is no matter range
                if(hasRun == false && y != 0){ // need this if statement to occur only once in the loop, and not occur if the range will have zero in it
                    hasRun = true;
                    g2.drawString("0", PADDING - 40, zeroLoc); // draws the zero, only want it to draw if the
                }
            }
            g2.drawString(Integer.toString(y), PADDING - 40, yPos); // draw on LEFT sidfe of y axis, the numbers
        }
    
        // Xassuming all employees share the same monthly data ordering, as it should be becaus eemployee class uses linked hashmap
        // we use the first active employee’s months if available. Otherwise, use an example.
        if (!activeEmployees.isEmpty()) { // calcualtes the spread of the months, if there were more id suspect this wouldnt work nicely lmao
            Employee sample = activeEmployees.get(0);
             List<Map.Entry<String, Double>> entries = sample.getMonthlyData().entrySet().stream().collect(Collectors.toList()); // chat helped with this for getting only the fist index
            int xStep = (getWidth() - 2 * PADDING) / entries.size(); // getwidth/2 gets the horizontal space, minus padding, and divides by the number of months, so we get space between
            for (int i = 0; i < entries.size(); i++) {
                int xPos = PADDING + (i * xStep) + (xStep / 2);
                g2.drawString(entries.get(i).getKey(), xPos - 10, getHeight() - PADDING + 20);
            }  
        }
    }

    private void displayExpected(Graphics2D g2, boolean flag) { // temporary fix, but still works, TODO: make it so if emplyoee didnt work, expected also does not get painted
        // this works exaclty like disaplayemployeedata, but because i want this to be seperably toggleable, i have to caputre current state and save it to be repainted
        if (flag) {
            Stroke originalStroke = g2.getStroke(); // save original stroke, which is basically currenlty what is dispalyed
            Stroke dashed = new BasicStroke(1.5f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,new float[]{10.0f},0.0f);
            g2.setStroke(dashed); // dashed pattern from chatgpt
            // g2.setColor(Color.darkGray); // all expected numerics are the same color

            // For each active employee, draw their expected performance.
            for (Employee employee : activeEmployees) {
                g2.setColor(employee.getDisplayColor()); 
                // Get the expected performance values (index 1).
                double expected  = employee.getExpected();
                int xStep = (getWidth() - 2 * PADDING) / employee.getMonthlyData().size();
                int prevX = -1;
                int prevY = -1;

                List<Map.Entry<String, Double>> entries = employee.getMonthlyData().entrySet().stream().collect(Collectors.toList()); // for month key for calculation

                for (int i = 0; i < employee.getMonthlyData().size(); i++) {
                    if (expected != -100.05) {
                        int x = PADDING + (i * xStep) + (xStep / 2);
                        int y = mapY(expected);

                        dataPoints.add(new PointValue(new Point(x, y), expected, employee, true, entries.get(i).getKey())); // for hovering

                        // draw the circle just as we do with actual performance
                        g2.fillOval(x - 3, y - 3, 6, 6);
                        if (prevX != -1) { // past first iteration or last value was not empty
                            g2.drawLine(prevX, prevY, x, y);
                        }
                        prevX = x;
                        prevY = y;
                    } else {
                        prevX = -1;
                        prevY = -1;
                    }
                }
            }
            // set the stroke wiht the data from method below after we draw the expected data
            g2.setStroke(originalStroke);
        }
    }

    
    private void drawEmployeeData(Graphics2D g2, Employee employee) {
        List<Map.Entry<String, Double>> entries = employee.getMonthlyData().entrySet().stream().collect(Collectors.toList()); // chat helped with this, gets the first index in the list
;       // get the employees specifc numbers to be plotted, chat helped with this one liner
        int xStep = (getWidth() - 2 * PADDING) / entries.size(); // calculkate step just like above so they are in line
        int prevX = -1, prevY = -1; // for drawing lines, -1 cuz no point at start
    
        Color empColor = employee.getDisplayColor() != null ? employee.getDisplayColor() : Color.BLACK; // default color of black otherwise its whatevey obj is
        g2.setColor(empColor);
    
        for (int i = 0; i < entries.size(); i++) { 
            double value = entries.get(i).getValue(); // for each entry get the value
            if(value != -100.05){ // value to indicate the employee did not work in this month
                int x = PADDING + (i * xStep) + (xStep / 2); // calculate again bruh, can be more efficent
                int y = mapY(value); // convert to pixels, play around wiht value because i think the formatting of strings and dots makes the dots appear vertically skewed downward
                // i had a minus ten from above, that was causing everything to be skewed upwards, this fixes that issue, need to change jar
                dataPoints.add(new PointValue(new Point(x, y), value, employee, false, entries.get(i).getKey())); // save for hover
        
                g2.fillOval(x - 3, y - 3, 6, 6); // draw it, chose random values until it looked like a point
        
                // Draw line from previous point if available.
                if (prevX != -1) { // if we are pasted the first iteration
                    g2.drawLine(prevX, prevY, x, y); // draw a line, given previous and cur points
                }
                prevX = x; // rotate
                prevY = y;
            } else {
                prevX = -1;
                prevY = -1;
            }

        }
    }
    
    private int mapY(double value) { // covnert to an x cordinate, a data value
        double scaleY = (getHeight() - 2 * PADDING) / (double) (yAxisMax - yAxisMin); // get the availdible vertial space,  divide by the cur max-min range, and its a doulve cuz decimal
        return (int) (getHeight() - PADDING - (value - yAxisMin) * scaleY); // get the bottom of the graph. subtract the min y axis to get the y level, and * scalay and turn to int
    }
    
    private static class PointValue { // might need to move in new file
        Point point;
        double value;
        Employee employee;
        boolean expected;
        String month;
    
        PointValue(Point point, double value, Employee employee, boolean expected, String month) { // this goes into a list that is dataPoints for hovering
            this.month = month;
            this.expected = expected;
            this.point = point;
            this.value = value;
            this.employee = employee; // might also change this so it contains the color, then when you hover the name is in the same color
        }
    }
}
