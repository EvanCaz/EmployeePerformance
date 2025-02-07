# 📄 Employee Perfromance Line Graph in Java with Swing

- Created by Evan Cazares for use at MWM Design Group

## ℹ️ Overview

Encompassed in 1.0, I created a Java Swing app that visualizes employee metrics for readability. It parses an Excel file saved as a CSV, gathers all the employee information, and displays it dynamically. Each employee is assigned a unique color, and their data is plotted on the graph. The user can change the range of the graph, and it will also update it according to the months present in the CSV file. The graph is in real-time and updates at the user's leisure.

1.0.1: Fixed plotting math issue where everything was skewed upwards by ten pixels; added horizontal line.

1.0.2: Fixed minor bugs and figured out GitHub release publishing.

1.1.0: Expected performance is now a switch, and dynamic range always keeps things as focused as can be. Bug handling of if the name does not contain a comma was not implemented. This is not published for personal reasons.

1.1.1: little fixes and dynamic range issues

1.1.2: quality of life features and set for the dropdown menu

1.2.0: Add a feature to allow clicking of two points; after selecting two, the tooltip at the mouse will display the percent gained or lost. Addiitonaly, a feature to determine how far off a specifc point when clicked is from the expected point, and if clicking two points belonging to two different employees, but in the same month, display how much better one performed over the other.

1.3.0: Will also add the ability to display certain groups. I know employees are classfied as a group, so another dropdown menu, or control function, will be used to display certain groups. In the CSV file, each employee, in the fifth column, contains their group. This can be added as an attribute.

1.4.0: add a feature that displays all employees who averaged positive numbers entirely or average a positive number of months, same with negative.

## 🚀 Usage instructions

It is now an .exe file located in the published section on GitHub. The folder includes the .exe as well as the JRE folder, all compressed together.

## 🌟 Highlights

![Graph 1.2.2](https://github.com/EvanCaz/EmployeePerformance/blob/main/Graph.png?raw=true)


![Decision Tree](https://github.com/EvanCaz/EmployeePerformance/blob/main/Tree.png?raw=true)


![Class Overview](https://github.com/EvanCaz/EmployeePerformance/blob/main/Diagram.png?raw=true)
