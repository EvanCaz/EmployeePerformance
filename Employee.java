import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.Color;

public class Employee { // TODO: change the strucutre to hold expected values, and change everythign else so it works with a new hashmap, so double is now "List double"
    private final String name;
    private double expected;
    private final Map<String, Double> monthlyData;
    private Color displayColor;
    private String org; // this will be set in the parser, access column five and set this variable

    public Employee(String name) {
        this.name = name;
        this.monthlyData = new LinkedHashMap<>(); // keepinsert order, so the months stay in the right order
        this.displayColor = Color.BLACK;
    }

    public void setDisplayColor(Color color){
        this.displayColor = color; // mihgt change this so it is the same color every time
    }
    public Color getDisplayColor(){return displayColor;}

    // setter and getter for org varaible
    public void setOrg(String x){
        this.org = x;
    }

    public String getOrg(){
        return org;
    }
    // setter monthly values
    public void setMonthValue(String monthName, double value) {
        monthlyData.put(capitalize(monthName.toLowerCase()), value); // might need to capitlize the first letter, whcih i can do below
    }

    public void setExpected(double value){
        this.expected = value;
    }
    public double getExpected(){
        return expected;
    }

    // getter for name
    public String getName() {
        return name;
    }

    // getter for monthly data
    public Map<String, Double> getMonthlyData() {
        return monthlyData;
    }

    

    // public void printMonthlyData() { // testing
    //     System.out.println("\nEmployee: " + name);
    //     monthlyData.forEach((month, value) -> 
    //         System.out.printf("%-10s: %.1f%n", 
    //             capitalize(month), // Use helper method
    //             value
    //         )
    //     );
    // }
    
    private String capitalize(String str) { // googled this
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}