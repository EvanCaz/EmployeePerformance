# 📄 Employee Perfromance Line Graph in Java with Swing

- Created by Evan Cazares for use at MWM Design Group

## ℹ️ Overview

Encompassed in 1.0, I created a Java Swing App that visualizes employee metrics for readability. It parse a excel file saved as a csv, gathers all the employee information, and displays it dynamically. Each employee is assigned a unique color and their data is plotted on the graph. The user can change the range of the graph, and it will also update accordingly to the months present in the csv file. The graph is in real time and updates at the users leisure. 

1.0.1, Fixed plotting math issue where everything was skewed upwards by ten pixels, add horizontal line.

1.0.2. Fixed minor bugs and figured out github release publishing. 

1.1 will have expected performance plotted with a switch to turn on and off, as well as potential issues/edge cases I have thought off handled correctly. This includes the way the name is parsed from the file. Some individuals may not have a last name, or their name may be entered into the excel file differently. Right now, a comma is used to seperate the first and last name, but in a csv file a comma dictates a row. From the start, the data of interest in the excel file is one column over from the corresponding month, but because of the comma in the name, the parser does not need the additional + 1 column count. This could cause an issue. Additionaly, more control flow options will be added, such as choose a new file without closing. 

Will also add the ability to display certain groups. I know employees are classfied as a group, so another dropdown menu, or control function, will be used to display certain groups. In the csv file, each employee, in the fifth column, contains their group. This can be added as an attribute.

## 🚀 Usage instructions

Is now a .exe file located in published section on github. Folder includes the .exe as well as the jre folder, all compressed togethor.

## 🌟 Highlights

![Class Overview](https://github.com/EvanCaz/EmployeePerformance/blob/main/Graph.png?raw=true)


![Decision Tree](https://github.com/EvanCaz/EmployeePerformance/blob/main/Tree.png?raw=true)


![Class Overview](https://github.com/EvanCaz/EmployeePerformance/blob/main/Diagram.png?raw=true)
