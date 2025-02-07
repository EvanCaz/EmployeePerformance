import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
// import java.util.stream.Collectors;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setDialogTitle("Select CSV File");
            
            int userSelection = fileChooser.showOpenDialog(null);  // centers dialog

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String csvFile = selectedFile.getAbsolutePath();
                Parser parser = new Parser(csvFile);

                try {
                    List<Employee> employees = parser.parse(); // parse the selected CSV file
                    employees.sort(Comparator.comparing(Employee::getName)); // and sort them
                    if (!employees.isEmpty()) {
                        createAndShowGUI(employees);
                    } else {
                        JOptionPane.showMessageDialog(null, "No employees found in the file.",
                                                      "Parsing Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error reading CSV file: " + e.getMessage(),
                                                  "File Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // User canceled file selection; exit gracefully.
                System.out.println("File selection canceled. Exiting.");
                System.exit(0);
            }
        });
    }

    private static void createAndShowGUI(List<Employee> employees) {
        JFrame frame = new JFrame("Employee Performance Graph"); // initlize the frame for everything
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close when i close it
        frame.setLayout(new BorderLayout()); // how I plane to arrange everything inside
    
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // area for all the buttons and drop down, "controlpanel" is created and flowlayout means the stuff is in a row
        controlPanel.setBackground(Color.LIGHT_GRAY);
        
        JComboBox<String> employeeDropdown = new JComboBox<>( // jcombo is the swing drop down, 
            employees.stream()
                     .map(Employee::getName) // maps all the names of the emoployees with getName method of my employee class
                     .toArray(String[]::new) // converts this toa  string array
        );
        employeeDropdown.setSelectedIndex(-1); // initial selection is none
        
        JButton yAxisButton = new JButton("Change Range"); // change the range of the graph button
        
        JButton displayAllButton = new JButton("Display All"); // button defintions for addition control buttons
        JButton clearAllButton = new JButton("Clear All");
        JToggleButton toggleExpectedPerformance = new JToggleButton("Expected Performance Off");
    
        controlPanel.add(employeeDropdown); // adding all of these to the flowlayout jpanel
        controlPanel.add(yAxisButton);
        controlPanel.add(displayAllButton);
        controlPanel.add(clearAllButton);
        controlPanel.add(toggleExpectedPerformance);
        
        JLabel activeEmployeesLabel = new JLabel("Active Employees: None"); // default selection is none, this is position right below controlPanel
        activeEmployeesLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        activeEmployeesLabel.setBackground(Color.LIGHT_GRAY);
        Graph graph = new Graph(); // creating graph object, constructor requires nothing but it used to and forhot to change it earlier
    
        JPanel graphContainer = new JPanel(new BorderLayout()); // another jpanel to containt it
        graphContainer.add(activeEmployeesLabel, BorderLayout.NORTH); 
        graphContainer.add(graph, BorderLayout.CENTER);
        graphContainer.setBackground(Color.LIGHT_GRAY); 
    
        frame.add(controlPanel, BorderLayout.NORTH); // the controlPanel is added to the top of this new jpanel
        frame.add(graphContainer, BorderLayout.CENTER); // the graph is added to the center, but realisticlay the everything else besides north
    
        Runnable updateLabel = () -> updateActiveEmployeesLabel(activeEmployeesLabel, graph); // chatgpt helped, idk what runnable is but this is a listener to update active employees
    
        // listenrs
    
        employeeDropdown.addActionListener(e -> {
            int selectedIndex = employeeDropdown.getSelectedIndex();
            if (selectedIndex >= 0) { // iff when i selkect something from dropdown, update the active employee, it resets too 
                Employee selectedEmployee = employees.get(selectedIndex);
                graph.toggleEmployee(selectedEmployee);
                updateLabel.run();

                employeeDropdown.setSelectedIndex(employeeDropdown.getSelectedIndex()); // resets here
            }
        });
    
        yAxisButton.addActionListener(e -> showYAxisDialog(graph)); // open the smaller boc to chagne range
        
        displayAllButton.addActionListener(e -> {
            graph.setEmployees(employees);
            updateLabel.run();
        });
    
        clearAllButton.addActionListener(e -> {
            graph.clearEmployees();
            updateLabel.run();
        });
    
        toggleExpectedPerformance.addActionListener(e -> {
            boolean showExpected = toggleExpectedPerformance.isSelected();
            if (showExpected) {
                toggleExpectedPerformance.setText("Expected Performance On");
            } else {
                toggleExpectedPerformance.setText("Expected Performance Off");
            }
            //  change variable withs etter so graph knows the change overlay
            graph.setShowExpected(showExpected);
            updateLabel.run();
        });
        

        
        


        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); //swing stuff
    }

    private static void updateActiveEmployeesLabel(JLabel label, Graph graph) { // accepts the label that contains active employees
        List<Employee> active = graph.getActiveEmployees();
        if (active.isEmpty()) {
            label.setText("Active Employees: None"); // default to none
        } else {
            // Build an HTML string that styles each employee's name with their display color.
            StringBuilder sb = new StringBuilder("<html>Active Employees: "); // html string with employees color
            for (int i = 0; i < active.size(); i++) {
                Employee emp = active.get(i);
                Color c = emp.getDisplayColor();
                // Convert the color to a hexadecimal string.
                String hexColor = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                sb.append("<span style='color:").append(hexColor).append(";'>")
                  .append(emp.getName())
                  .append("</span>");
                if (i < active.size() - 1) {
                    sb.append(" | ");
                }
            }
            sb.append("</html>");
            label.setText(sb.toString());
        }
    }

    private static void showYAxisDialog(Graph graph) { // this should never be used outside the class
        JPanel panel = new JPanel(new GridLayout(2, 2)); // this containts the input fields below, kinda backwards
        
        JTextField minField = new JTextField("-25"); // default to these values which are same in graph class, these are the creation of both inut fields
        JTextField maxField = new JTextField("25");
        
        panel.add(new JLabel("Y-Axis Minimum:")); // adding labels to the fields and addings corresponding text from a second ago
        panel.add(minField);
        panel.add(new JLabel("Y-Axis Maximum:"));
        panel.add(maxField);
    
        int result = JOptionPane.showConfirmDialog( // display the dialgo box when pressed
            null, // center in the middle of window
            panel,
            "Set Y-Axis Range", // title
            JOptionPane.OK_CANCEL_OPTION // built in two options
        );
    
        if (result == JOptionPane.OK_OPTION) { // if it is an okay option or valid
            try {
                int newMin = Integer.parseInt(minField.getText()); // get the text convert to integer and store it for pasing into my graph object
                int newMax = Integer.parseInt(maxField.getText());
                graph.setYAxisRange(newMin, newMax); // pass here
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog( // if bad option, display below
                    null,
                    "Invalid input. Please enter valid integers where min < max.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
