import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Graph extends JPanel {
    private static final int PADDING = 90; // margins
    private int yAxisMin = -25; // going to change this so it is dynamic, where the min is rounded to the next lowest 5 and max is rounded to next highest five, so the spread of lines is greatest no matter what
    private int yAxisMax = 25;
    
    private Point hoverPoint = null; // default null cuz nothing is displayed yet
    private String hoverText = null;
    
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
        repaint(); // change the graph again
    }
    
    public void setEmployees(List<Employee> employees) { // refresh and start over, hence the clear, add all, and repaint
        activeEmployees.clear();
        activeEmployees.addAll(employees);
        repaint();
    }
    
    public void clearEmployees() { // clear all 
        activeEmployees.clear();
        repaint();
    }
    
    public boolean hasAll(List<Employee> allEmployees) {
        return activeEmployees.containsAll(allEmployees) && (activeEmployees.size() == allEmployees.size()); // check in from enitre list whos active
    }
    
    public void setYAxisRange(int min, int max) { // for chaning when thebutton is pressed
        if (min >= max) {
            throw new IllegalArgumentException("Y-axis min must be less than max"); // this is what is caught by main
        }
        this.yAxisMin = min;
        this.yAxisMax = max;
        repaint();
    }
    
    private void checkHover(int mouseX, int mouseY) {
        final int HOVER_THRESHOLD = 5; // how clsoe we consider it to be, in pixels, to display data
        hoverPoint = null;
        hoverText = null;
        for (PointValue pv : dataPoints) { // if it is in the dataPoints, then we display the corresponding employee stuff
            if (Math.abs(pv.point.x - mouseX) <= HOVER_THRESHOLD && Math.abs(pv.point.y - mouseY) <= HOVER_THRESHOLD) { // uses absolute value to check if BOTH x and y are within the threshold
                hoverPoint = new Point(mouseX, mouseY); // if they are, change the variable to the current point
                hoverText = String.format("%s: %.1f", pv.employee.getName().chars().mapToObj(c -> String.valueOf((char) c)).takeWhile(c -> !c.equals(",")).reduce("", String::concat), pv.value); // and display, googled inline maping so i onyl get the last name
                break; // stop when u find one
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // clr data points before drawing

        dataPoints.clear(); // clear data pojnts so we dont get overlapping and only the ones currently draawn are there
        
        Graphics2D g2 = (Graphics2D) g; // set to 2d for more capabliteis
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // chatgpt helped me with this, makes for a better image
    
        g2.setColor(Color.LIGHT_GRAY); //backround
        g2.fillRect(0, 0, getWidth(), getHeight()); // fill entire rectangle
    
        drawAxes(g2); // draw axis, chatgpt helped here again with this method
    
        
        for (Employee emp : activeEmployees) { // for all employes, draw their stuff
            drawEmployeeData(g2, emp);
        }
    
        drawHoverTooltip(g2); // hover tooltip if neded
    }
    
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
            List<Map.Entry<String, Double>> entries = sample.getMonthlyData().entrySet().stream().collect(Collectors.toList());
            int xStep = (getWidth() - 2 * PADDING) / entries.size(); // getwidth/2 gets the horizontal space, minus padding, and divides by the number of months, so we get space between
            for (int i = 0; i < entries.size(); i++) {
                int xPos = PADDING + (i * xStep) + (xStep / 2);
                g2.drawString(entries.get(i).getKey(), xPos - 10, getHeight() - PADDING + 20);
            }  
        }
    }
    
    private void drawEmployeeData(Graphics2D g2, Employee employee) {
        List<Map.Entry<String, Double>> entries = employee.getMonthlyData().entrySet().stream().collect(Collectors.toList()); // get the employees specifc numbers to be plotted, chat helped with this one liner
        int xStep = (getWidth() - 2 * PADDING) / entries.size(); // calculkate step just like above so they are in line
        int prevX = -1, prevY = -1; // for drawing lines, -1 cuz no point at start
    
        Color empColor = employee.getDisplayColor() != null ? employee.getDisplayColor() : Color.BLUE; // default color of blue otherwise its whatevey obj is
        g2.setColor(empColor);
    
        for (int i = 0; i < entries.size(); i++) { 
            double value = entries.get(i).getValue(); // for each entry get the value
            if(value != -100.05){ // value to indicate the employee did not work in this month
                int x = PADDING + (i * xStep) + (xStep / 2); // calculate again bruh, can be more efficent
                int y = mapY(value); // convert to pixels, play around wiht value because i think the formatting of strings and dots makes the dots appear vertically skewed downward
                // i had a minus ten from above, that was causing everything to be skewed upwards, this fixes that issue, need to change jar
                dataPoints.add(new PointValue(new Point(x, y), value, employee)); // save for hover
        
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
    
        PointValue(Point point, double value, Employee employee) { // this goes into a list that is dataPoints for hovering
            this.point = point;
            this.value = value;
            this.employee = employee; // might also change this so it contains the color, then when you hover the name is in the same color
        }
    }
}
