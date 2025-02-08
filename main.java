import javax.swing.*;
import javax.swing.border.Border;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
// import java.util.stream.Collectors;

public class main {

    private static Set<Integer> prevEmpindex = new HashSet(); // quality of life stuff
    private static Set<String> orgs = new HashSet<>();
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
                        JOptionPane.showMessageDialog(null, "No employees found in the file.","Parsing Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error reading CSV file: " + e.getMessage(),"File Error", JOptionPane.ERROR_MESSAGE);
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
    
        JPanel controlPanelNorth = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // area for all the buttons and drop down, "controlPanelNorth" is created and flowlayout means the stuff is in a row
        // JPanel controlPanelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // for extra buttons
        controlPanelNorth.setBackground(Color.LIGHT_GRAY);
        // controlPanelSouth.setBackground(Color.LIGHT_GRAY);
        
        JComboBox<String> employeeDropdown = new JComboBox<>( // jcombo is the swing drop down, 
            employees.stream()
                     .map(Employee::getName) // maps all the names of the emoployees with getName method of my employee class
                     .toArray(String[]::new) // converts this toa  string array
        );
        employeeDropdown.setSelectedIndex(-1); // initial selection is none
        
        JComboBox<String> orgDropdown = new JComboBox<>(employees.stream().map(Employee::getOrg).collect(Collectors.toSet()).toArray(new String[0])); // same as above
        orgDropdown.setSelectedIndex(-1); // clear it

        // JButton yAxisButton = new JButton("Change Range"); // change the range of the graph button
        // JButton calDifference = new JButton("Calculate Difference");
        JButton displayAllButton = new JButton("Display All"); // button defintions for addition control buttons
        JButton clearAllButton = new JButton("Clear All");
        JToggleButton toggleExpectedPerformance = new JToggleButton("Expected Performance Off");
    
        controlPanelNorth.add(employeeDropdown); // adding all of these to the flowlayout jpanel
        controlPanelNorth.add(orgDropdown);
        controlPanelNorth.add(displayAllButton);
        controlPanelNorth.add(clearAllButton);
        controlPanelNorth.add(toggleExpectedPerformance);
        
        JLabel activeEmployeesLabel = new JLabel("Active Employees: None"); // default selection is none, this is position right below controlPanelNorth
        // JLabel difference = new JLabel("Difference: None");
        activeEmployeesLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        activeEmployeesLabel.setBackground(Color.LIGHT_GRAY);
        Graph graph = new Graph(); // creating graph object, constructor requires nothing but it used to and forhot to change it earlier
    
        JPanel graphContainer = new JPanel(new BorderLayout()); // another jpanel to containt it
        graphContainer.add(activeEmployeesLabel, BorderLayout.NORTH); 
        // graphContainer.add(difference, BorderLayout.SOUTH);
        graphContainer.add(graph, BorderLayout.CENTER);
        graphContainer.setBackground(Color.LIGHT_GRAY); 
    
        frame.add(controlPanelNorth, BorderLayout.NORTH); // the controlPanelNorth is added to the top of this new jpanel
        frame.add(graphContainer, BorderLayout.CENTER); // the graph is added to the center, but realisticlay the everything else besides north
        // frame.add(controlPanelSouth, BorderLayout.SOUTH);
        Runnable updateLabel = () -> updateActiveEmployeesLabel(activeEmployeesLabel, graph); // chatgpt helped, idk what runnable is but this is a listener to update active employees
    
        // listenrs
    
        employeeDropdown.addActionListener(e -> { // need to fix so then when selecting same on it clears
            int selectedIndex = employeeDropdown.getSelectedIndex();
            Employee selectedEmployee = employees.get(selectedIndex);
            if(prevEmpindex.isEmpty() == true || !prevEmpindex.contains(selectedIndex)){
                // System.out.println("Testing 1: " + prevEmpindex);
                orgDropdown.setSelectedIndex(-1);
                graph.toggleEmployee(selectedEmployee);
                updateLabel.run();
                prevEmpindex.add(selectedIndex);
                employeeDropdown.setSelectedIndex(selectedIndex);
            } else if(prevEmpindex.contains(selectedIndex)){
                // System.out.println("Testing 2: " + prevEmpindex);
                graph.toggleEmployee(selectedEmployee);
                updateLabel.run();
                prevEmpindex.remove(selectedIndex);
                employeeDropdown.setSelectedIndex(-1);
            } 
        });

        orgDropdown.addActionListener(e -> {
            int selectedIndex = orgDropdown.getSelectedIndex();
            String selectedOrg = (String) orgDropdown.getSelectedItem();
            graph.clearEmployees();
            // employeeDropdown.setSelectedIndex(-1);
            for(Employee emp : employees){
                if(emp.getOrg() == selectedOrg){
                    graph.toggleEmployee(emp);
                }
            }
            orgDropdown.setSelectedIndex(selectedIndex);
        });

        displayAllButton.addActionListener(e -> {
            graph.setEmployees(employees);
            orgDropdown.setSelectedIndex(-1);
            updateLabel.run();
        });
    
        clearAllButton.addActionListener(e -> {
            graph.clearEmployees();
            orgDropdown.setSelectedIndex(-1);
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

    // private static void updateDifferenceLabel(JLabel difference, Graph graph){
    //     String status = graph.getDifferenceLabel();
    //     if (status.isEmpty()) {
    //         difference.setText("Difference: None"); // default to none
    //     } else {
    //         difference.setText((status));
    //     }
    // }

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
}
