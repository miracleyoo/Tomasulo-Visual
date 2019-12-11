# Tomasulo-Visual

If you are just learning Tomasulo algorithm, please don't miss this repo, it will show you how it works in a quite straightforward way. 

This is a visualization project of Tomasulo algorithm which make it easier to understand the algorithm. It can read a `*.s` asm file and parse it, and you can execute instructions according to Tomasulo algorithm. This project is mainly for study use and it will not really parse the instruction to binary and it just briefly translate all of the instructions to a set of characterized classes and assign a separate execute function to each class. 

## File Structure

`asm_code/`: Sample asm code for test.
`Assets/`: UI related images and screen captures.
`src/com/miracleyoo/Logic/MainLogic.java`: Main logic. All of the important global variables are placed here and you can go into the code to read the very detailed comment of every variable. Also, the whole logic and related functions under the `Execute step` can be find here.
`src/com/miracleyoo/UIs/CoolMainUI.java`: The welcome page. It is boarder-less, image-based and mouse-movable. It will guide you to select a file to execute. More detailed introduction can be find below in the screen capture's explanation.
`src/com/miracleyoo/UIs/DataUI.java`: The most important UI, we mostly work here and it will show us a cycle table, register table, tomasulo dynamic chart, statistics and data table.
`src/com/miracleyoo/UIs/Diagram.java`: Tomasulo Dynamic Chart's implementation. All manually draw graphic components.
`src/com/miracleyoo/UIs/ArchitectureCycleUI.java`: UI that takes the input of cycle numbers of each type of function unit.
`src/com/miracleyoo/UIs/ArchitectureNumUI.java`: UI that takes the input of exact numbers of each type of function unit.
`src/com/miracleyoo/UIs/MultiStepsUI.java`: UI that takes the "multi" of "multi step".
`src/com/miracleyoo/UIs/InfoUI.java`: UI that shows the info of program and authors.

## Main Logic Workflow

```

          Initialize data structures and dictionaries
                            ↓
                Parse the next instruction
                            ↓
               Judge whether it can be issued now
                            ↓
  Update the Information for the newly issued instruction (like issue cycle)
                            ↓
  Sequentially check all of the instructions in the Operands station now,
         And do corresponding operation to them according to state
          ↓               ↓                   ↓               ↓
 | check can exe | check can exeEnd |  check can WB |  check can end |
          ↓                                   ↓               ↓
          ↓                                 ExeOps          EndOps
          ↓
  OpsNOP / OpsADD / OpsSUB / OpsMUL / OpsDIV / ......
                            ↓
                         cycle ++
```

## Some screen captures and introduction

At first the welcome page, it will guide the user open a file ends with `*.s` and parse the file and pass it to Data UI.
![Welcome_Page](./Assets/Screen_Capture/Welcome_Page.png)

You will be able to select files you want to parse here, it will be originally set to the `./asm_code` folder, you can change the default setting if you want, in the `CoolMainUI` code. This folder contains 4 asm files which are already tested and they work properly.
![Select_file](./Assets/Screen_Capture/Select_file.png)

Here is the DataUI, this UI contains the most of the UI content and it includes a Cycle Table, a Tomasulo Diagram, a Registers Table, a Data Table and a Statistics. They will all update properly as you click the `Execute 1/multiple Step` button. One thing to mention is that the Tomasulo Dynamic Chart is drew manually, and it will always be in the center view. 
![Data_UI_Empty](./Assets/Screen_Capture/Data_UI_Empty.png)

Here is a screen capture of a file being executed halfway, and you can see cycles and Tomasulo Graph can work according to the Tomasulo theory and each instruction will have a special highlight color, both in cycles table and in Tomasulo Dynamic Chart. You can track every instruction by its color. All there items will update as you click button.
![Data_UI_Middle](./Assets/Screen_Capture/Data_UI_Middle.png)

This is the state when all the instruction of a asm file has been finished. The Op Queue is empty and all of the Cycle table items are filled with specific cycle stage number.
![Data_UI_Final](./Assets/Screen_Capture/Data_UI_Final.png)

This is the input panel where user can input the cycle number needed for the completion of an operation for each kind of function unit. The program will be reset but with the same input file when you change some items here, since the structure changes and program need to restart.
![Function_Unit_Cycle_Setting](./Assets/Screen_Capture/Function_Unit_Cycle_Setting.png)

This is the input panel where user can input the number of each kind of function unit, you can see the change of FU number in Tomasulo Dynamic Chart and Statistics. The program will be reset but with the same input file when you change some items here, since the structure changes and program need to restart.
![Function_Unit_Number_Setting](./Assets/Screen_Capture/Function_Unit_Number_Setting.png)

You may have noticed that in the bottom right corner there is an button named `Execute multiple steps`, and the input page here is for defining the `multi`.
![Multi_Step_Setting](./Assets/Screen_Capture/Multi_Step_Setting.png)

Here is the help or information page. You will find basic information about the program and its authors here.
![Information_Page](./Assets/Screen_Capture/Information_Page.png)

Also, the program has two themes! Light and Dark mode are supported. They will change smoothly and nothing will be reset. In fact I prefer the dark mode more. The default mode can be changed in the `MainLogic.java` file.
![Dark_Mode_Theme](./Assets/Screen_Capture/Dark_Mode_Theme.png)

ReadMe authors: **Zhongyang, Vignesh**



