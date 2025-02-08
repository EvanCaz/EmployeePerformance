import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.Color;

public class Parser {
    private final String csvFile;

    public Parser(String csvFile) {
        this.csvFile = csvFile;
    }

    public List<Employee> parse() throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // line 1 is useless
            String line2 = br.readLine(); // months
            br.readLine(); // line 3 is useless to me as well

            String line;
            while ((line = br.readLine()) != null) { // keep reading the lines until they are empty
                if (line.startsWith("\"")) { // Only process employee rows
                    // Parse employee name and data values
                    String[] parsedData = parseNameAndData(line); // get the information for this line
                    String name = parsedData[0]; // name is aklways first
                    String[] dataValues = Arrays.copyOfRange(parsedData, 1, parsedData.length); // copy the rest of the indexes to an array
                    //  works even if additional months are present, but not sure if it will if not in same column as values
                    List<Integer> monthIndices = parseMonthIndices(line2);
                    Employee employee = new Employee(name); // new obj
                    employee.setExpected(Double.parseDouble(parsedData[6])); // should get expected values now, i intepreted the graph wrong
                    for (int monthColumn : monthIndices) {
                        String monthName = line2.split(",")[monthColumn].trim(); // access the array at index in month idices to get the name
                        int valueIndex = monthColumn; // get the index for the value we want, 
                        if (valueIndex < dataValues.length) { // if it is in the range, capture it
                            double value = parseValue(dataValues[valueIndex].trim()); // trim and capture at the same index, but in our row
                            employee.setMonthValue(monthName, value); // initlize the object
                        }
                    }
                    employees.add(employee); // add to the list of obs
                }
            }
        }

        assignUniqueColors(employees);

        return employees; // return
    }

    private void assignUniqueColors(List<Employee> employees) { // come up wiht unique colors
        float hue = (float) Math.random(); //  random hue value in [0,1)
        final float goldenRatioConjugate = 0.618033988749895f; // googled it
        for (Employee employee : employees) {
            hue += goldenRatioConjugate; 
            hue %= 1; // 0 to 1 range.
            // brightness and saturation colors below, adjust to change outlook, made it so easier on eyes
            Color uniqueColor = Color.getHSBColor(hue, 0.8f, 0.55f);
            employee.setDisplayColor(uniqueColor);
        }
    }
    

    // Extracts month indices from the header line.
    private List<Integer> parseMonthIndices(String line2) {
        String[] line2Split = line2.split(","); // split but month value, the csv should also contain the same number of , before the first month is listed, as there are other columns in the excel sheet
        List<Integer> monthIndices = new ArrayList<>(); // this will extract the months correctly, but if they are eery different, this and the data corresponding will be skewed
        for (int i = 0; i < line2Split.length; i++) {
            if (!line2Split[i].trim().isEmpty()) { // in any of the values in the commas, if the are not empty, they contain a month, always in row2/line2
                monthIndices.add(i);
            }
        }
        return monthIndices;
    }

    private String[] parseNameAndData(String line) {
        List<String> result = new ArrayList<>();
        if (line.startsWith("\"")) {
            int endQuote = line.indexOf('"', 1);
            result.add(line.substring(1, endQuote).trim()); // get the name
            String dataPart = line.substring(endQuote); // the plus two here was cutting of the data i want for expected
            Collections.addAll(result, dataPart.split("\\s*,\\s*"));
        } else {
            String[] parts = line.split(",");
            result.add(parts[0].trim());
            Collections.addAll(result, Arrays.copyOfRange(parts, 1, parts.length));
        }
        return result.toArray(new String[0]);
    }

    private static double parseValue(String valueStr) { // updated to handle 0
        try {
            if (valueStr.equals("-") || valueStr.equals("#N/A")) {
                return -100.05; // arbitrary value, i am pretty sure no one will every score that low so i can use -100.05 as a placehold for they didnt work that month
            }
            if (valueStr.startsWith("(") && valueStr.endsWith(")")) { // if string, which can be accessed with java stuff, starts and ends with () it is negative, i also didnt know that is what () meant and had to ask my dad
                String numStr = valueStr.substring(1, valueStr.length() - 1).trim(); // trim whitespace, and the indexes are after and befroe alst index whihc is negative
                return -Double.parseDouble(numStr); // retrun it negative
            }
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid value: " + valueStr + ". Defaulting to 0.0");
            return 0.0;
        }
    }
}
