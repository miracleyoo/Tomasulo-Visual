package com.miracleyoo.UIs;

import com.miracleyoo.utils.Instruction;
import com.miracleyoo.utils.InstructionTrack;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static java.lang.StrictMath.min;

public class Diagram extends JPanel {

    int tick = 0; //Used to update the Diagram only when the execute button has been pressed

    Font labelFont = new Font("Arial", Font.BOLD, 10); //Font for labels on Diagram
    Font normalFont = new Font("Arial", Font.PLAIN, 9); //Font for instructions that traverse Diagram


    //Standardize block widths/heights on display
    static int height = 12;
    static int opBoxWidth = 30;
    static int operandWidth = 50;

    //Designate number of RS per Functional unit here
    int sdBuffer = (int) DataUI.mainLogic.architectureNum[0];
    int ldBuffer = (int) DataUI.mainLogic.architectureNum[1];
    int integerRS = (int) DataUI.mainLogic.architectureNum[2];
    int fpAdderRS = (int) DataUI.mainLogic.architectureNum[3];
    int fpMultiplierRS = (int) DataUI.mainLogic.architectureNum[4];
    int fpDividerRS = (int) DataUI.mainLogic.architectureNum[5];
    int registers = 10; //# of register slots to display on the daigram

    //Allows for window scaling while keeping objects in their relative positions
    public int diagramWidth = 600 + 200 + 50 + 50; // Most left: -200; Most right: 200 + 50(rect width)
    public int diagramHeight = 110 + height * DataUI.mainLogic.OpQueue + height + 30; // Most down: -110; Most top: Reg rects top

    //blank Instruction and InstructionTrack templates for init of RS/buffers and flushing
    Instruction blank = new Instruction("", "", "", "", "", 0, 0, -1);
    InstructionTrack opBlank = new InstructionTrack("", 0);

    //Data strucs to hold the data to display on diagram provided by MainLogic
    Instruction[] opQArr = new Instruction[DataUI.mainLogic.OpQueue];
    String[] opQ = new String[DataUI.mainLogic.OpQueue];
    Instruction[] ldArr = new Instruction[ldBuffer];
    Instruction[] sdArr = new Instruction[sdBuffer];
    Instruction[] intArr = new Instruction[integerRS];
    Instruction[] addArr = new Instruction[fpAdderRS];
    Instruction[] mulArr = new Instruction[fpMultiplierRS];
    Instruction[] divArr = new Instruction[fpDividerRS];
    String[] regArr = new String[registers];
    InstructionTrack[] testArr = new InstructionTrack[DataUI.mainLogic.OpQueue]; //Used for OpQueue highlights
    InstructionTrack[] rArr = new InstructionTrack[registers]; //Used for register highlights


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diagramWidth, diagramHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        paintComponent((Graphics2D) g);
    }

    //Set thickness of the lines used in the diagram
    private void drawThickLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setStroke(new BasicStroke(3));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(new BasicStroke(1));
    }

    public void setCycleNum(int c) {
        //Diagram is able to keep track of and display current cycle number
        DataUI.mainLogic.CycleNumCur = c;
    }

    //---PAINTCOMPONENTS OF DIAGRAM---
    protected void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //Set diagram color scheme
        int originX = getWidth() / 2 - 25;// getViewport().getSize().width;// getWidth()/2;
        int originY = getHeight() / 2 + 35; //getViewport().getSize().height;//getHeight()/2;

        g.drawString(Integer.toString(DataUI.mainLogic.CycleNumCur), originX, originY); //debugging to keep track of updating diagram in sync with DataUI.mainLogic.CycleNumCur

        //Place OpQueue -- Holds the instructions that have not been issued
        g.setFont(labelFont);
        g.drawString("OP Queue", originX - 100, originY - (height * DataUI.mainLogic.OpQueue + height) - 60);
        for (int q = 0; q < DataUI.mainLogic.OpQueue; q++) {
            g.drawRect(originX - 100, originY - (height * q + height) - 60, 100, height);
        }

        g.setFont(normalFont);
        if(!DataUI.mainLogic.isEnd) { //Checks if there are valid instructions remaining in InstructionFullList
            for (int j = 0; j < DataUI.mainLogic.OpQueue; j++) {
                //if opQArr has a blank position, push next awaiting instruction
                if (testArr[j] == null || testArr[j] == opBlank) {
                    String temp = "";

                    testArr[j].absIndex = DataUI.mainLogic.instructionLineCur + j; //grabbing the index of the instruction
                    if (DataUI.mainLogic.instructionLineCur + j < DataUI.mainLogic.InstructionFullList.size()){ //Stay within bounds of InstructionFullList
                        //remove any tags/comments before storing into array
                        if (DataUI.mainLogic.InstructionFullList.get(DataUI.mainLogic.instructionLineCur + j).split(":").length > 1) {
                            temp = DataUI.mainLogic.InstructionFullList.get(DataUI.mainLogic.instructionLineCur + j).split(":")[1].trim();
                            testArr[j].str = temp.split(";")[0].trim(); //place just the raw instruction in the opQ
                        } else {
                            testArr[j].str = DataUI.mainLogic.InstructionFullList.get(DataUI.mainLogic.instructionLineCur + j).split(";")[0].trim(); //place just the raw instruction in the opQ
                        }
                        if(!testArr[j].str.equals("")) {
                            //display on GUI if there is a valid value in the array at j
                            g.setColor(Color.decode(DataUI.colorSchemeCycleCur[testArr[j].absIndex % DataUI.colorSchemeCycleCur.length])); //for highlight
                            g.fillRect(originX - 100 + 1, originY - (height * j + height) - 60 + 1, 99, height - 1);
                            g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                            g.drawString(testArr[j].str, originX - 100 + 5, originY - (height * j) - 62);
                        }
                    }
                }
            }
        }
        else{
            Arrays.fill(testArr, opBlank); //fill the OpQ with blanks once MainLogic has reached the end of the InstructionFullList
        }

        //---Place ldBuffer---\\
        int[] ldBase = {-400, -60}; //Sets x,y coordinate base values to ensure all ldBuffer components are relative to each other
        g.setFont(labelFont);
        g.drawString("LD Buffer (From Memory)", originX + ldBase[0], originY - (height * ldBuffer + height) + ldBase[1]);
        for (int i = 0; i < ldBuffer; i++) {
            g.drawRect(originX + ldBase[0], originY - (height * i + height) + ldBase[1], 50, height);
        }

        g.setFont(normalFont);
        for (int z = 0; z < ldBuffer; z++) {
            //paint ld instructions on diagram
            if (ldArr[z] != null && ldArr[z] != blank) {
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[ldArr[z].index % DataUI.colorSchemeCycleCur.length])); //for highlight
                g.fillRect(originX + ldBase[0] + 1, originY - (height * z + height) + ldBase[1] + 1, 49, height-1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(ldArr[z].op, originX + ldBase[0] + 5, originY + ldBase[1] - (height * z) - 2);
            }
        }

        //---Place sdBuffer---\\
        int[] sdBase = {250, -60}; //Sets x,y coordinate base values to ensure all sdBuffer components are relative to each other
        g.setFont(labelFont);
        g.drawString("SD Buffer (To Memory)", originX + sdBase[0] - 50, originY - (height * sdBuffer + height) + sdBase[1]);
        for (int i = 0; i < sdBuffer; i++) {
            g.drawRect(originX + sdBase[0], originY - (height * i + height) + sdBase[1], 50, height);
        }

        g.setFont(normalFont);
        for (int z = 0; z < sdBuffer; z++) {
            //paint sd instructions on diagram
            if (sdArr[z] != null && sdArr[z] != blank) {
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[sdArr[z].index % DataUI.colorSchemeCycleCur.length])); //for highlight
                g.fillRect(originX + sdBase[0] + 1, originY - (height * z + height) + sdBase[1] + 1, 49, height-1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(sdArr[z].op, originX + sdBase[0] + 5, originY + sdBase[1] - (height * z) - 2);
            }
        }

        //---Place Registers---\\
        g.setFont(labelFont);
        g.drawString("Registers", originX + 50, originY - (height * registers + height) - 60);
        for (int q = 0; q < registers; q++) {
            g.drawRect(originX + 50, originY - (height * q + height) - 60, 80, height);
        }
        g.setFont(normalFont);

        //Highlight and paint instructions in registers on Diagram
        for(int j = 0; j < min(registers, DataUI.mainLogic.wbList.size()); j++){
            if (rArr[j] == null || rArr[j] == opBlank) {
                rArr[j].absIndex = DataUI.mainLogic.wbList.get(j).absoluteIndex; //grabbing the index of the instruction
                rArr[j].str = DataUI.mainLogic.wbList.get(j).operation; //Grabbing the string of instruction
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[rArr[j].absIndex % DataUI.colorSchemeCycleCur.length])); //Highlight
                g.fillRect(originX + 50 + 1, originY - (height * j + height) - 60 + 1, 79, height - 1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(rArr[j].str, originX + 50 + 5, originY - (height * j) - 62);
            }
        }




        //Place integer FU
        int intBase[] = {-300, 60}; //used to set origin of int FU on Tomasulo graph. X and then Y.
        for (int a = 0; a < integerRS; a++) {
            g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //Set color
            //place Operation box
            g.drawRect(originX - opBoxWidth + intBase[0], originY - (height * a) + intBase[1], opBoxWidth, height);
            //Place srcReg boxes
            g.drawRect(originX + intBase[0], originY - (height * a) + intBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + intBase[0], originY - (height * a) + intBase[1], operandWidth, height);
            g.drawString("IntegerFU", originX + intBase[0] + 5, originY + intBase[1] + 30);
            g.drawRect(originX + intBase[0], originY + intBase[1] + 20, 80, height);
            g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
            g.setStroke(new BasicStroke(5));
            drawThickLine(g, originX + intBase[0] + 40, originY + intBase[1] + 34, originX + intBase[0] + 40, originY + 100); //Arrow to CDB
            g.fillPolygon(new int[]{originX + intBase[0] + 35, originX + intBase[0] + 40, originX + intBase[0] + 45}, new int[]{originY + 99, originY + 109, originY + 99}, 3);
            drawThickLine(g, originX + intBase[0] + 90, originY + intBase[1] + 17, originX + intBase[0] + 90, originY + 110); //Arrow from CDB
            g.fillPolygon(new int[]{originX + intBase[0] + 85, originX + intBase[0] + 90, originX + intBase[0] + 95}, new int[]{originY + intBase[1] + 22, originY + intBase[1] + 12, originY + intBase[1] + 22}, 3);
        }

        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        for (int z = 0; z < integerRS; z++) {
            //paint corresponding data on Diagram
            if (intArr[z] != null && intArr[z] != blank) {
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[intArr[z].index % DataUI.colorSchemeCycleCur.length])); //Highlight
                g.fillRect(originX - opBoxWidth + intBase[0] + 1, originY - (height * z) + intBase[1] + 1, opBoxWidth-1, height - 1);
                g.fillRect(originX + intBase[0] + 1, originY - (height * z) + intBase[1] + 1, operandWidth - 1, height - 1);
                g.fillRect(originX + operandWidth + intBase[0] + 1, originY - (height * z) + intBase[1] + 1, operandWidth - 1, height - 1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(intArr[z].op, originX + intBase[0] + 5 - opBoxWidth - 2, originY + intBase[1] - (height * z - height) - 2);
                g.drawString(intArr[z].src1, originX + intBase[0] + 5, originY + intBase[1] - (height * z - height) - 2);
                g.drawString(intArr[z].src2, originX + operandWidth + intBase[0] + 5, originY + intBase[1] - (height * z - height) - 2);
            }
        }

        //Place fp adder FU
        int addBase[] = {-150, 60}; //used to set origin of adder FU on Tomasulo graph. X and then Y.
        for (int x = 0; x < fpAdderRS; x++) {
            g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
            g.drawRect(originX - opBoxWidth + addBase[0], originY - (height * x) + addBase[1], opBoxWidth, height);
            g.drawRect(originX + addBase[0], originY - (height * x) + addBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + addBase[0], originY - (height * x) + addBase[1], operandWidth, height);
            g.drawString("FPadder", originX + addBase[0] + 5, originY + addBase[1] + 30);
            g.drawRect(originX + addBase[0], originY + addBase[1] + 20, 80, height);
            g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
            drawThickLine(g, originX + addBase[0] + 40, originY + addBase[1] + 34, originX + addBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[]{originX + addBase[0] + 35, originX + addBase[0] + 40, originX + addBase[0] + 45}, new int[]{originY + 99, originY + 109, originY + 99}, 3);
            drawThickLine(g, originX + addBase[0] + 90, originY + addBase[1] + 17, originX + addBase[0] + 90, originY + 110); //from CDB
            g.fillPolygon(new int[]{originX + addBase[0] + 85, originX + addBase[0] + 90, originX + addBase[0] + 95}, new int[]{originY + addBase[1] + 22, originY + addBase[1] + 12, originY + addBase[1] + 22}, 3);
        }
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        for (int z = 0; z < fpAdderRS; z++) {
            ////paint corresponding data on Diagram
            if (addArr[z] != blank && addArr[z] != null) {
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[addArr[z].index % DataUI.colorSchemeCycleCur.length])); //need to insert absoluteValue of instruction here
                g.fillRect(originX - opBoxWidth + addBase[0] + 1, originY - (height * z) + addBase[1] + 1, opBoxWidth-1, height - 1);
                g.fillRect(originX + addBase[0] + 1, originY - (height * z) + addBase[1] + 1, operandWidth - 1, height - 1);
                g.fillRect(originX + operandWidth + addBase[0] + 1, originY - (height * z) + addBase[1] + 1, operandWidth - 1, height - 1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(addArr[z].op, originX - opBoxWidth + addBase[0] + 5 - 2, originY + addBase[1] - (height * z - height) - 2);
                g.drawString(addArr[z].src1, originX + addBase[0] + 5, originY + addBase[1] - (height * z - height) - 2);
                g.drawString(addArr[z].src2, originX + operandWidth + addBase[0] + 5, originY + addBase[1] - (height * z - height) - 2);
            }
        }

        //fp multiplier FU
        int mulBase[] = {0, 60}; //used to set origin of multiplier FU on Tomasulo graph. X and then Y
        for (int y = 0; y < fpMultiplierRS; y++) {
            g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
            g.drawRect(originX - opBoxWidth + mulBase[0], originY - (height * y) + mulBase[1], opBoxWidth, height);
            g.drawRect(originX + mulBase[0], originY - (height * y) + mulBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + mulBase[0], originY - (height * y) + mulBase[1], operandWidth, height);
            g.drawString("FPmult", originX + mulBase[0] + 5, originY + mulBase[1] + 30);
            g.drawRect(originX + mulBase[0], originY + mulBase[1] + 20, 80, height);
            g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
            drawThickLine(g, originX + mulBase[0] + 40, originY + mulBase[1] + 34, originX + mulBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[]{originX + mulBase[0] + 35, originX + mulBase[0] + 40, originX + mulBase[0] + 45}, new int[]{originY + 99, originY + 109, originY + 99}, 3);
            drawThickLine(g, originX + mulBase[0] + 90, originY + mulBase[1] + 17, originX + mulBase[0] + 90, originY + 110); //from CDB
            g.fillPolygon(new int[]{originX + mulBase[0] + 85, originX + mulBase[0] + 90, originX + mulBase[0] + 95}, new int[]{originY + mulBase[1] + 22, originY + mulBase[1] + 12, originY + mulBase[1] + 22}, 3);
        }
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        for (int z = 0; z < fpMultiplierRS; z++) {
            //paint corresponding data on Diagram
            if (mulArr[z] != null && mulArr[z] != blank) {
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[mulArr[z].index % DataUI.colorSchemeCycleCur.length])); //need to insert absoluteValue of instruction here
                g.fillRect(originX - opBoxWidth + mulBase[0] + 1, originY - (height * z) + mulBase[1] + 1, opBoxWidth-1, height - 1);
                g.fillRect(originX + mulBase[0] + 1, originY - (height * z) + mulBase[1] + 1, operandWidth - 1, height - 1);
                g.fillRect(originX + operandWidth + mulBase[0] + 1, originY - (height * z) + mulBase[1] + 1, operandWidth - 1, height - 1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(mulArr[z].op, originX + mulBase[0] + 5 - opBoxWidth - 2, originY + mulBase[1] - (height * z - height) - 2);
                g.drawString(mulArr[z].src1, originX + mulBase[0] + 5, originY + mulBase[1] - (height * z - height) - 2);
                g.drawString(mulArr[z].src2, originX + operandWidth + mulBase[0] + 5, originY + mulBase[1] - (height * z - height) - 2);
            }
        }

        //fp Div FU
        int divBase[] = {150, 60}; //used to set origin of divider FU on Tomasulo graph. X and then Y.
        for (int z = 0; z < fpDividerRS; z++) {
            g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
            g.drawRect(originX - opBoxWidth + divBase[0], originY - (height * z) + divBase[1], opBoxWidth, height);
            g.drawRect(originX + divBase[0], originY - (height * z) + divBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + divBase[0], originY - (height * z) + divBase[1], operandWidth, height);
            g.drawString("FPdiv", originX + divBase[0] + 5, originY + divBase[1] + 30);
            g.drawRect(originX + divBase[0], originY + divBase[1] + 20, 80, height);
            g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
            drawThickLine(g, originX + divBase[0] + 40, originY + divBase[1] + 34, originX + divBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[]{originX + divBase[0] + 35, originX + divBase[0] + 40, originX + divBase[0] + 45}, new int[]{originY + 99, originY + 109, originY + 99}, 3);
            drawThickLine(g, originX + divBase[0] + 90, originY + divBase[1] + 17, originX + divBase[0] + 90, originY + 110); //from CDB
            g.fillPolygon(new int[]{originX + divBase[0] + 85, originX + divBase[0] + 90, originX + divBase[0] + 95}, new int[]{originY + divBase[1] + 22, originY + divBase[1] + 12, originY + divBase[1] + 22}, 3);
        }
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        for (int z = 0; z < fpDividerRS; z++) {
            //paint corresponding data on Diagram
            if (divArr[z] != null && divArr[z] != blank) {
                g.setColor(Color.decode(DataUI.colorSchemeCycleCur[divArr[z].index % DataUI.colorSchemeCycleCur.length])); //need to insert absoluteValue of instruction here
                g.fillRect(originX - opBoxWidth + divBase[0] + 1, originY - (height * z) + divBase[1] + 1, opBoxWidth-1, height - 1);
                g.fillRect(originX + divBase[0] + 1, originY - (height * z) + divBase[1] + 1, operandWidth - 1, height - 1);
                g.fillRect(originX + operandWidth + divBase[0] + 1, originY - (height * z) + divBase[1] + 1, operandWidth - 1, height - 1);
                g.setColor(Color.decode(DataUI.colorSchemeMainCur[6])); //whatever the scheme color is for the text
                g.drawString(divArr[z].op, originX + divBase[0] + 5 - opBoxWidth - 2, originY + divBase[1] - (height * z - height) - 2);
                g.drawString(divArr[z].src1, originX + divBase[0] + 5, originY + divBase[1] - (height * z - height) - 2);
                g.drawString(divArr[z].src2, originX + operandWidth + divBase[0] + 5, originY + divBase[1] - (height * z - height) - 2);
            }
        }


        g.setColor(Color.BLACK);
        //***---Diagram Logic---***\\
        //Classify instruction type on each execute cycle and place within the appropriate buffer/RS array
        if (tick != DataUI.mainLogic.CycleNumCur) {
            flushBuffers(); //Clear buffers and rewrite every clock cycle

            for (int i = 0; i < DataUI.mainLogic.OperationInfoStation.size(); i++) {
                //Create respective Reservation Station arrays to hold instructions while they execute load on clock cycle
                if (DataUI.mainLogic.OperationInfoStation.get(i).state.equals("Issue") || DataUI.mainLogic.OperationInfoStation.get(i).state.equals("EXE") || DataUI.mainLogic.OperationInfoStation.get(i).equals("ExeEnd")) { //Hold in RS ExeEnd if CBD is occupied
                    switch (DataUI.mainLogic.OperationInfoStation.get(i).op) {
                        case "LOAD":
                            for (int z = 0; z < ldBuffer; z++) {
                                //insert into load buffer if there is a blank space
                                if (ldArr[z] == blank || ldArr[z] == null) {
                                    ldArr[z] = new Instruction(DataUI.mainLogic.OperationInfoStation.get(i).operation, DataUI.mainLogic.OperationInfoStation.get(i).DestReg, DataUI.mainLogic.OperationInfoStation.get(i).s1, DataUI.mainLogic.OperationInfoStation.get(i).s2, DataUI.mainLogic.OperationInfoStation.get(i).state, DataUI.mainLogic.OperationInfoStation.get(i).currentStageCycleNum, DataUI.mainLogic.OperationInfoStation.get(i).exeStart, DataUI.mainLogic.OperationInfoStation.get(i).absoluteIndex);
                                    break;
                                }
                            }
                            break;

                        case "SAVE":
                            for (int z = 0; z < sdBuffer; z++) {
                                //insert into save buffer if there is a blank space
                                if (sdArr[z] == blank || sdArr[z] == null) {
                                    sdArr[z] = new Instruction(DataUI.mainLogic.OperationInfoStation.get(i).operation, DataUI.mainLogic.OperationInfoStation.get(i).DestReg, DataUI.mainLogic.OperationInfoStation.get(i).s1, DataUI.mainLogic.OperationInfoStation.get(i).s2, DataUI.mainLogic.OperationInfoStation.get(i).state, DataUI.mainLogic.OperationInfoStation.get(i).currentStageCycleNum, DataUI.mainLogic.OperationInfoStation.get(i).exeStart, DataUI.mainLogic.OperationInfoStation.get(i).absoluteIndex);
                                    break;
                                }
                            }
                            break;

                        case "INT":
                            for (int z = 0; z < integerRS; z++) {
                                //insert into int RS if there is a blank space
                                if (intArr[z] == blank || intArr[z] == null) {
                                    intArr[z] = new Instruction(DataUI.mainLogic.OperationInfoStation.get(i).operation, DataUI.mainLogic.OperationInfoStation.get(i).DestReg, DataUI.mainLogic.OperationInfoStation.get(i).s1, DataUI.mainLogic.OperationInfoStation.get(i).s2, DataUI.mainLogic.OperationInfoStation.get(i).state, DataUI.mainLogic.OperationInfoStation.get(i).currentStageCycleNum, DataUI.mainLogic.OperationInfoStation.get(i).exeStart, DataUI.mainLogic.OperationInfoStation.get(i).absoluteIndex);
                                    break;
                                }
                            }

                            break;

                        case "ADD":
                            for (int z = 0; z < fpAdderRS; z++) {
                                //insert into Add RS if there is a blank space
                                if (addArr[z] == blank || addArr[z] == null) {
                                    addArr[z] = new Instruction(DataUI.mainLogic.OperationInfoStation.get(i).operation, DataUI.mainLogic.OperationInfoStation.get(i).DestReg, DataUI.mainLogic.OperationInfoStation.get(i).s1, DataUI.mainLogic.OperationInfoStation.get(i).s2, DataUI.mainLogic.OperationInfoStation.get(i).state, DataUI.mainLogic.OperationInfoStation.get(i).currentStageCycleNum, DataUI.mainLogic.OperationInfoStation.get(i).exeStart, DataUI.mainLogic.OperationInfoStation.get(i).absoluteIndex);
                                    break;
                                }
                            }

                            break;

                        case "MUL":
                            for (int z = 0; z < fpMultiplierRS; z++) {
                                //insert into mul RS if there is a blank space
                                if (mulArr[z] == blank || mulArr[z] == null) {
                                    mulArr[z] = new Instruction(DataUI.mainLogic.OperationInfoStation.get(i).operation, DataUI.mainLogic.OperationInfoStation.get(i).DestReg, DataUI.mainLogic.OperationInfoStation.get(i).s1, DataUI.mainLogic.OperationInfoStation.get(i).s2, DataUI.mainLogic.OperationInfoStation.get(i).state, DataUI.mainLogic.OperationInfoStation.get(i).currentStageCycleNum, DataUI.mainLogic.OperationInfoStation.get(i).exeStart, DataUI.mainLogic.OperationInfoStation.get(i).absoluteIndex);
                                    break;
                                }
                            }

                            break;

                        case "DIV":
                            for (int z = 0; z < fpDividerRS; z++) {
                                //insert into div RS if there is a blank space
                                if (divArr[z] == blank || divArr[z] == null) {
                                    divArr[z] = new Instruction(DataUI.mainLogic.OperationInfoStation.get(i).operation, DataUI.mainLogic.OperationInfoStation.get(i).DestReg, DataUI.mainLogic.OperationInfoStation.get(i).s1, DataUI.mainLogic.OperationInfoStation.get(i).s2, DataUI.mainLogic.OperationInfoStation.get(i).state, DataUI.mainLogic.OperationInfoStation.get(i).currentStageCycleNum, DataUI.mainLogic.OperationInfoStation.get(i).exeStart, DataUI.mainLogic.OperationInfoStation.get(i).absoluteIndex);
                                    break;
                                }
                            }

                            break;

                        case "BRA":
                            //if Branch
                            break;

                        case "NOP":
                            //do nothing
                            break;

                        case "HALT":
                            //stop prgm
                            break;
                    }
                }
            }
            tick++;
        }


        //---Connecting Wires---
        g.setColor((Color.decode(DataUI.colorSchemeMainCur[6]))); //follow dataUI color scheme
        g.setStroke(new BasicStroke(3)); //wire width

        //CBD wires
        g.setFont(labelFont);
        g.drawString("Common Data Bus", originX - 400, originY + 120);
        g.setFont(normalFont);
        g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
        drawThickLine(g, originX + ldBase[0] + 25, originY + ldBase[1] + 2, originX + ldBase[0] + 25, originY + 105); //Vertical arrow from ld Buffer to CBD
        g.fillPolygon(new int[]{originX + ldBase[0] + 20, originX + ldBase[0] + 25, originX + ldBase[0] + 30}, new int[]{originY + 100, originY + 110, originY + 100}, 3);
        drawThickLine(g, originX + ldBase[0], originY + 110, originX + sdBase[0] + 100, originY + 110); //CBD horizontal line
        drawThickLine(g, originX + sdBase[0] + 100, originY + sdBase[1] - registers * height + 5, originX + sdBase[0] + 100, originY + 110); //CBD vertical line going up to store data
        drawThickLine(g, originX + sdBase[0] + 55, originY + sdBase[1] - sdBuffer * height + 5, originX + sdBase[0] + 100, originY + sdBase[1] - sdBuffer * height + 5); //Horizontal line to SD buffer
        g.fillPolygon(new int[]{originX + sdBase[0] + 50, originX + sdBase[0] + 60, originX + sdBase[0] + 60}, new int[]{originY + sdBase[1] - sdBuffer * height + 5, originY + sdBase[1] - sdBuffer * height + 10, originY + sdBase[1] - sdBuffer * height}, 3);
        drawThickLine(g, originX + 55 + 80, originY + sdBase[1] - registers * height + 5, originX + sdBase[0] + 100, originY + sdBase[1] - registers * height + 5); //Horizontal line connecting to Registers
        g.fillPolygon(new int[]{originX + 50 + 80, originX + 50 + 90, originX + 50 + 90}, new int[]{originY + sdBase[1] - registers * height + 5, originY + sdBase[1] - registers * height + 10, originY + sdBase[1] - registers * height}, 3);

        //OpQueue Wires
        g.setColor(Color.decode(DataUI.colorSchemeCycleCur[2]));
        drawThickLine(g, originX - 100 + 40, originY - 60 + 2, originX - 100 + 40, originY - 40); //Vertical line connecting Op-Queue
        drawThickLine(g, originX - opBoxWidth + intBase[0] + 15, originY + intBase[1] - (integerRS - 1) * height - 5, originX - opBoxWidth + intBase[0] + 15, originY - 40); //Vertical line connecting intOp
        g.fillPolygon(new int[]{originX - opBoxWidth + intBase[0] + 10, originX - opBoxWidth + intBase[0] + 15, originX - opBoxWidth + intBase[0] + 20}, new int[]{originY + intBase[1] - (integerRS - 1) * height - 10, originY + intBase[1] - (integerRS - 1) * height, originY + intBase[1] - (integerRS - 1) * height - 10}, 3);
        drawThickLine(g, originX - opBoxWidth + addBase[0] + 15, originY + addBase[1] - (fpAdderRS - 1) * height - 5, originX - opBoxWidth + addBase[0] + 15, originY - 40); //Vertical line connecting fpAddOp
        g.fillPolygon(new int[]{originX - opBoxWidth + addBase[0] + 10, originX - opBoxWidth + addBase[0] + 15, originX - opBoxWidth + addBase[0] + 20}, new int[]{originY + addBase[1] - (fpAdderRS - 1) * height - 10, originY + addBase[1] - (fpAdderRS - 1) * height, originY + addBase[1] - (fpAdderRS - 1) * height - 10}, 3);
        drawThickLine(g, originX - opBoxWidth + mulBase[0] + 15, originY + mulBase[1] - (fpMultiplierRS - 1) * height - 5, originX - opBoxWidth + mulBase[0] + 15, originY - 40); //Vertical line connecting fpMulOp
        g.fillPolygon(new int[]{originX - opBoxWidth + mulBase[0] + 10, originX - opBoxWidth + mulBase[0] + 15, originX - opBoxWidth + mulBase[0] + 20}, new int[]{originY + mulBase[1] - (fpMultiplierRS - 1) * height - 10, originY + mulBase[1] - (fpMultiplierRS - 1) * height, originY + mulBase[1] - (fpMultiplierRS - 1) * height - 10}, 3);
        drawThickLine(g, originX - opBoxWidth + divBase[0] + 15, originY + divBase[1] - (fpDividerRS - 1) * height - 5, originX - opBoxWidth + divBase[0] + 15, originY - 40); //Vertical line connecting fpDivOp
        g.fillPolygon(new int[]{originX - opBoxWidth + divBase[0] + 10, originX - opBoxWidth + divBase[0] + 15, originX - opBoxWidth + divBase[0] + 20}, new int[]{originY + divBase[1] - (fpDividerRS - 1) * height - 10, originY + divBase[1] - (fpDividerRS - 1) * height, originY + divBase[1] - (fpDividerRS - 1) * height - 10}, 3);
        drawThickLine(g, originX - opBoxWidth + intBase[0] + 15, originY - 40, originX - opBoxWidth + divBase[0] + 15, originY - 40); //Horizontal connecting line

        //src1 line
        g.setColor(Color.decode(DataUI.colorSchemeCycleCur[1]));
        drawThickLine(g, originX + 50 + 10, originY - 60 + 2, originX + 50 + 10, originY - 30); //Vertical line from register connecting to src1
        drawThickLine(g, originX + intBase[0] + 25, originY + intBase[1] - (integerRS - 1) * height - 5, originX + intBase[0] + 25, originY - 30); //Vertical line for intRS src1
        g.fillPolygon(new int[]{originX + intBase[0] + 20, originX + intBase[0] + 25, originX + intBase[0] + 30}, new int[]{originY + intBase[1] - (integerRS - 1) * height - 10, originY + intBase[1] - (integerRS - 1) * height, originY + intBase[1] - (integerRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + addBase[0] + 25, originY + addBase[1] - (fpAdderRS - 1) * height - 5, originX + addBase[0] + 25, originY - 30); //Vertical line for fpAdderRS src1
        g.fillPolygon(new int[]{originX + addBase[0] + 20, originX + addBase[0] + 25, originX + addBase[0] + 30}, new int[]{originY + addBase[1] - (fpAdderRS - 1) * height - 10, originY + addBase[1] - (fpAdderRS - 1) * height, originY + addBase[1] - (fpAdderRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + mulBase[0] + 25, originY + mulBase[1] - (fpMultiplierRS - 1) * height - 5, originX + mulBase[0] + 25, originY - 30); //Vertical line for fpMulRS src1
        g.fillPolygon(new int[]{originX + mulBase[0] + 20, originX + mulBase[0] + 25, originX + mulBase[0] + 30}, new int[]{originY + mulBase[1] - (fpMultiplierRS - 1) * height - 10, originY + mulBase[1] - (fpMultiplierRS - 1) * height, originY + mulBase[1] - (fpMultiplierRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + divBase[0] + 25, originY + divBase[1] - (fpDividerRS - 1) * height - 5, originX + divBase[0] + 25, originY - 30); //Vertical line for fpDivRS src1
        g.fillPolygon(new int[]{originX + divBase[0] + 20, originX + divBase[0] + 25, originX + divBase[0] + 30}, new int[]{originY + divBase[1] - (fpDividerRS - 1) * height - 10, originY + divBase[1] - (fpDividerRS - 1) * height, originY + divBase[1] - (fpDividerRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + intBase[0] + 25, originY - 30, originX + divBase[0] + 25, originY - 30); //Horizontal connecting line


        //src2 line
        g.setColor(Color.decode(DataUI.colorSchemeCycleCur[3]));
        drawThickLine(g, originX + 50 + 70, originY - 60 + 2, originX + 50 + 70, originY - 50); //Vertical line from register connecting to src2
        drawThickLine(g, originX + operandWidth + intBase[0] + 25, originY + intBase[1] - (integerRS - 1) * height - 5, originX + operandWidth + intBase[0] + 25, originY - 50); //Vertical line for intRS src2
        g.fillPolygon(new int[]{originX + operandWidth + intBase[0] + 20, originX + operandWidth + intBase[0] + 25, originX + operandWidth + intBase[0] + 30}, new int[]{originY + intBase[1] - (integerRS - 1) * height - 10, originY + intBase[1] - (integerRS - 1) * height, originY + intBase[1] - (integerRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + operandWidth + addBase[0] + 25, originY + addBase[1] - (fpAdderRS - 1) * height - 5, originX + operandWidth + addBase[0] + 25, originY - 50); //Vertical line for fpAdderRS src2
        g.fillPolygon(new int[]{originX + operandWidth + addBase[0] + 20, originX + operandWidth + addBase[0] + 25, originX + operandWidth + addBase[0] + 30}, new int[]{originY + addBase[1] - (fpAdderRS - 1) * height - 10, originY + addBase[1] - (fpAdderRS - 1) * height, originY + addBase[1] - (fpAdderRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + operandWidth + mulBase[0] + 25, originY + mulBase[1] - (fpMultiplierRS - 1) * height - 5, originX + operandWidth + mulBase[0] + 25, originY - 50); //Vertical line for fpMulRS src2
        g.fillPolygon(new int[]{originX + operandWidth + mulBase[0] + 20, originX + operandWidth + mulBase[0] + 25, originX + operandWidth + mulBase[0] + 30}, new int[]{originY + mulBase[1] - (fpMultiplierRS - 1) * height - 10, originY + mulBase[1] - (fpMultiplierRS - 1) * height, originY + mulBase[1] - (fpMultiplierRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + operandWidth + divBase[0] + 25, originY + divBase[1] - (fpDividerRS - 1) * height - 5, originX + operandWidth + divBase[0] + 25, originY - 50); //Vertical line for fpDivRS src2
        g.fillPolygon(new int[]{originX + operandWidth + divBase[0] + 20, originX + operandWidth + divBase[0] + 25, originX + operandWidth + divBase[0] + 30}, new int[]{originY + divBase[1] - (fpDividerRS - 1) * height - 10, originY + divBase[1] - (fpDividerRS - 1) * height, originY + divBase[1] - (fpDividerRS - 1) * height - 10}, 3);
        drawThickLine(g, originX + operandWidth + intBase[0] + 25, originY - 50, originX + operandWidth + divBase[0] + 25, originY - 50); //Horizontal connecting line


        repaint();
    }

    public void flushBuffers() {
        //Function to flush all buffers
        Arrays.fill(opQArr, blank);
        Arrays.fill(ldArr, blank);
        Arrays.fill(sdArr, blank);
        Arrays.fill(intArr, blank);
        Arrays.fill(addArr, blank);
        Arrays.fill(mulArr, blank);
        Arrays.fill(divArr, blank);
        Arrays.fill(regArr, "");
        Arrays.fill(opQ, "");
        Arrays.fill(testArr, opBlank);
        Arrays.fill(rArr, opBlank);
    }



}

/*
Instruction[] opQArr = new Instruction[DataUI.mainLogic.OpQueue];
    Instruction[] ldArr = new Instruction[ldBuffer];
    Instruction[] sdArr = new Instruction[sdBuffer];
    Instruction[] intArr = new Instruction[integerRS];
    Instruction[] addArr = new Instruction[fpAdderRS];
    Instruction[] mulArr = new Instruction[fpMultiplierRS];
    Instruction[] divArr = new Instruction[fpDividerRS];
    Instruction[] regArr = new Instruction[registers];
 */