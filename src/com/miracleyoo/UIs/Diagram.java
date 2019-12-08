package com.miracleyoo.UIs;

import com.miracleyoo.utils.Instruction;
import com.miracleyoo.Logic.MainLogic;
import com.miracleyoo.utils.TableUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Diagram extends JPanel {

    private int cycleNumOld = 0;
    private int instrIndex = 0; //this will need to change to keep track of instructions
    int tick = 0;

    int fontSize = 9;

    //Standardize block widths/heights
    public static int height = 12;
    static int opBoxWidth = 30;
    static int operandWidth = 50;

    //Designate number of RS per Functional unit here
    int sdBuffer = (int) MainLogic.architectureNum[0];
    int ldBuffer = (int) MainLogic.architectureNum[1];
    int integerRS = (int) MainLogic.architectureNum[2];
    int fpAdderRS = (int) MainLogic.architectureNum[3];
    int fpMultiplierRS = (int) MainLogic.architectureNum[4];
    int fpDividerRS = (int) MainLogic.architectureNum[5];

    static int registers = 10;
    public static int diagramWidth = 600 + 200 + 50 + 50; // Most left: -200; Most right: 200 + 50(rect width)
    public static int diagramHeight = 110 + height * MainLogic.OpQueue + height + 30; // Most down: -110; Most top: Reg rects top
    //Allows for window scaling while keeping objects in their relative positions

    Instruction blank = new Instruction("", "", "", "", "", 0, 0);
    Instruction[] opQArr = new Instruction[MainLogic.OpQueue];
    String[] opQ = new String[MainLogic.OpQueue];
    Instruction[] ldArr = new Instruction[ldBuffer];
    Instruction[] sdArr = new Instruction[sdBuffer];
    Instruction[] intArr = new Instruction[integerRS];
    Instruction[] addArr = new Instruction[fpAdderRS];
    Instruction[] mulArr = new Instruction[fpMultiplierRS];
    Instruction[] divArr = new Instruction[fpDividerRS];
    Instruction[] regArr = new Instruction[registers];
    String issueBuffer = "";

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diagramWidth, diagramHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        paintComponent((Graphics2D) g);
    }

    private void drawThickLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setStroke(new BasicStroke(3));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(new BasicStroke(1));
    }

    public void setCycleNum(int c) {
        MainLogic.CycleNumCur = c;
    }

    protected void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        int originX = getWidth() / 2 - 25;// getViewport().getSize().width;// getWidth()/2;
        int originY = getHeight() / 2 + 35; //getViewport().getSize().height;//getHeight()/2;
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));


        g.drawString(Integer.toString(MainLogic.CycleNumCur), originX, originY); //debugging to keep track of updating diagram in sync with MainLogic.CycleNumCur

        //Place OpQueue -- Note Op Queue is currently implementing both int and fp, THIS MAY NEED TO CHANGE!
        g.drawString("OP Queue", originX - 100, originY - (height * MainLogic.OpQueue + height) - 60);
        for (int q = 0; q < MainLogic.OpQueue; q++) {
            g.drawRect(originX - 100, originY - (height * q + height) - 60, 80, height);
        }

        //Push instructions to opQueue --> Instructions stored here until ISSUED to prevent structural hazard
        //Shift instructions down as they are processed through the OpQueue in FIFO manner. In order issue one instruction at a time!

        //Push first 10 instructions onto opQArr initially at clk 0
        for (int o = 0; o < MainLogic.OpQueue; o++) {
            //if opQArr has a blank position, push next awaiting instruction
            if (opQArr[o] == null || opQArr[o] == blank) {
                //opQArr[o] = new Instruction(MainLogic.OperationInfoStation.getFirst().operand, MainLogic.OperationInfoStation.getFirst().DestReg, MainLogic.OperationInfoStation.getFirst().SourceReg1, MainLogic.OperationInfoStation.getFirst().SourceReg2, MainLogic.OperationInfoStation.getFirst().state, 0);//instr[instrIndex], "", "", "", 1 , 0);
                //opQArr[o] = new Instruction(MainLogic.OperationInfoFull.getLast().operand, MainLogic.OperationInfoFull.getLast().DestReg, MainLogic.OperationInfoFull.getLast().SourceReg1, MainLogic.OperationInfoFull.getLast().SourceReg2, MainLogic.OperationInfoFull.getLast().state, 0);
                //System.out.println("Instruction added: " + opQArr[o].op + " valO: " + o);
                opQ[o] = MainLogic.instr[o];
                break;
            }
        }


        for (int q = 0; q < MainLogic.OpQueue; q++) {
            if (opQArr[q] != null) {
                //g.drawString(opQArr[q].op, originX - 100 + 5, originY - (height * q) - 60);
                g.drawString(opQ[q], originX - 100 + 5, originY - (height * q) - 60);
            }
        }

/*
        //Try to push next instruction every clock cycle
        if (MainLogic.CycleNumCur != cycleNumOld) {
            //need to shift all elements in opQArr down by 1 index
            issueBuffer = opQArr[0].op;
            //System.out.println("issueBuffer holds: " + issueBuffer);
            for (int q = 0; q < MainLogic.OpQueue - 1; q++) {
                opQArr[q] = opQArr[q + 1];
            }
            //Check if instr array has awaiting instr.
            if (instrIndex < MainLogic.OperationInfoStation.size()) {
                opQArr[MainLogic.OpQueue - 1] = new Instruction(MainLogic.OperationInfoStation.get(instrIndex).operand, MainLogic.OperationInfoStation.get(instrIndex).DestReg,MainLogic.OperationInfoStation.get(instrIndex).SourceReg1,MainLogic.OperationInfoStation.get(instrIndex).SourceReg2, MainLogic.OperationInfoStation.get(instrIndex).state, MainLogic.CycleNumCur); //Grab next instruction from instruction array
                instrIndex++;
            }

            //If no more instructions, begin clearing the Opqueue
            else {
                opQArr[MainLogic.OpQueue - 1] = blank;
            }

            cycleNumOld = MainLogic.CycleNumCur;
        }
*/

        for (int q = 0; q < MainLogic.OpQueue; q++) {
            //g.setColor(Color.decode(DataUI.colorSchemeCycleCur[opQArr.length%DataUI.colorSchemeCycleCur.length])); //to set highlight
            //g.drawRect(originX - 100, originY - (height * q + height) - 60, 80, height);
            //g.drawString(opQArr[q].op, originX - 95, originY - (height * q) - 62);
        }


        //---ldBuffers---\\
        int[] ldBase = {-400, -60};
        g.drawString("LD Buffer (From Memory)", originX + ldBase[0], originY - (height * ldBuffer + height) + ldBase[1]);
        for (int i = 0; i < ldBuffer; i++) {
            g.drawRect(originX + ldBase[0], originY - (height * i + height) + ldBase[1], 50, height);
        }
        for(int z = 0; z < ldBuffer; z++) {
            //paint on diagram
            if (ldArr[z] != null || ldArr[z] != blank) {
                g.drawString(ldArr[z].op, originX + ldBase[0] + 5, originY + ldBase[1] - (height * z) - 2);
            }
        }


        //---sdBuffers---\\
        int[] sdBase = {250, -60};
        g.drawString("SD Buffer (To Memory)", originX + sdBase[0], originY - (height * sdBuffer + height) + sdBase[1]);
        for (int i = 0; i < sdBuffer; i++) {
            g.drawRect(originX + sdBase[0], originY - (height * i + height) + sdBase[1], 50, height);
        }
        for(int z = 0; z < sdBuffer; z++) {
            //paint on diagram
            if (sdArr[z] != null || sdArr[z] != blank) {
                g.drawString(sdArr[z].op, originX + sdBase[0] + 5, originY + sdBase[1] - (height * z) - 2);
            }
        }

        //---Registers---\\
        g.drawString("Int/FP Registers", originX + 50, originY - (height * registers + height) - 60);
        for (int q = 0; q < registers; q++) {
            g.drawRect(originX + 50, originY - (height * q + height) - 60, 80, height);
        }
        for(int z = 0; z < registers; z++) {
            //paint on diagram
            if (regArr[z] != null || regArr[z] != blank) {
                g.drawString(regArr[z].op, originX + 55, originY - (height * z) - 62);
            }
        }


        //Place integer FU
        int intBase[] = {-300, 60}; //x, y
        for (int a = 0; a < integerRS; a++) {
            g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
            g.drawRect(originX - opBoxWidth + intBase[0], originY - (height * a) + intBase[1], opBoxWidth, height);
            g.drawRect(originX + intBase[0], originY - (height * a) + intBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + intBase[0], originY - (height * a) + intBase[1], operandWidth, height);
            g.drawString("IntegerFU", originX + intBase[0] + 5, originY + intBase[1] + 30);
            g.drawRect(originX + intBase[0], originY + intBase[1] + 20, 80, height);
            g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
            g.setStroke(new BasicStroke(5));
            drawThickLine(g, originX + intBase[0] + 40, originY + intBase[1] + 34, originX + intBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[]{originX + intBase[0] + 35, originX + intBase[0] + 40, originX + intBase[0] + 45}, new int[]{originY + 99, originY + 109, originY + 99}, 3);
            drawThickLine(g, originX + intBase[0] + 90, originY + intBase[1] + 17, originX + intBase[0] + 90, originY + 110); //From CDB
            g.fillPolygon(new int[]{originX + intBase[0] + 85, originX + intBase[0] + 90, originX + intBase[0] + 95}, new int[]{originY + intBase[1] + 22, originY + intBase[1] + 12, originY + intBase[1] + 22}, 3);
        }
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        for(int z = 0; z < integerRS; z++) {
            //paint on diagram
            if (intArr[z] != null || intArr[z] != blank) {
                g.drawString(intArr[z].op, originX + intBase[0] + 5 - opBoxWidth, originY + intBase[1] - (height * z - height) - 2);
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
        for(int z = 0; z < fpAdderRS; z++) {
            //paint on diagram
            if (addArr[z] != blank || addArr[z] != null) {
                g.drawString(addArr[z].op, originX - opBoxWidth + addBase[0] + 5, originY + addBase[1] - (height * z - height) - 2);
                g.drawString(addArr[z].src1, originX + addBase[0] + 5, originY + addBase[1] - (height * z - height) - 2);
                g.drawString(addArr[z].src2, originX + operandWidth + addBase[0] + 5, originY + addBase[1] - (height * z - height) - 2);
            }
        }

        //fp multiplier FU
        int mulBase[] = {0, 60}; //used to set origin of multiplier FU on Tomasulo graph
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
        for(int z = 0; z < fpMultiplierRS; z++){
            //paint on diagram
            if (mulArr[z] != null || mulArr[z] != blank) {
                g.drawString(mulArr[z].op, originX + mulBase[0] + 5 - opBoxWidth, originY + mulBase[1] - (height * z - height) - 2);
                g.drawString(mulArr[z].src1, originX + mulBase[0] + 5, originY + mulBase[1] - (height * z - height) - 2);
                g.drawString(mulArr[z].src2, originX + operandWidth + mulBase[0] + 5, originY + mulBase[1] - (height * z - height) - 2);
            }
        }

        //fp Div FU
        int divBase[] = {150, 60};
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
        for(int z = 0; z < fpDividerRS; z++){
            //paint on diagram
            if (divArr[z] != null || divArr[z] != blank) {
                g.drawString(divArr[z].op, originX + divBase[0] + 5 - opBoxWidth, originY + divBase[1] - (height * z - height) - 2);
                g.drawString(divArr[z].src1, originX + divBase[0] + 5, originY + divBase[1] - (height * z - height) - 2);
                g.drawString(divArr[z].src2, originX + operandWidth + divBase[0] + 5, originY + divBase[1] - (height * z - height) - 2);
            }
        }

//       / boolean inserted = false;
        g.setColor(Color.BLACK);
        //***---Diagram Logic---***\\
        if(tick != MainLogic.OperationInfoStation.size()){
            boolean inserted = false;
            //flushBuffers();
            //updateState();

            for (int i = 0; i < MainLogic.OperationInfoStation.size(); i++) {



                //System.out.println(MainLogic.OperationInfoStation.get(i).state);
                //Create respective Reservation Station arrays to hold instructions while they execute load on clock cycle
                if (MainLogic.OperationInfoStation.get(i).state.equals("Issue") || MainLogic.OperationInfoStation.get(i).state.equals("EXE") || MainLogic.OperationInfoStation.get(i).equals("ExeEnd")) { //Hold in RS ExeEnd if CBD is occupied

                    //Color color = colorSchemeCycleCur[i]%DataUI.colorSchemeCycleCur.length]); //set highlight color of text
                    switch (MainLogic.OperationInfoStation.get(i).operand) {
                        case "LOAD":
                            for (int z = 0; z < ldBuffer; z++) {
                                //insert into load buffer if there is a blank space
                                if (!inserted && (ldArr[z] == blank || ldArr[z] == null)) {
                                    ldArr[z] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                                    //System.out.println("Load to buffer: "  + z + " " + ldArr[z].op);
                                    inserted = true;
                                    break;
                                }
                            }
                            break;

                        case "SAVE":
                            for (int z = 0; z < sdBuffer; z++) {
                                //insert if there is a blank space
                                if (!inserted && (sdArr[z] == blank || sdArr[z] == null)) {
                                    sdArr[z] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                                    //System.out.println("Save to memory");
                                    inserted = true;
                                    break;
                                }
                            }
                            break;

                        case "INT":
                            for (int z = 0; z < integerRS; z++) {
                                //insert if there is a blank space
                                if (!inserted && (intArr[z] == blank || intArr[z] == null)) {
                                    intArr[z] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                                    //System.out.println("Integer op detected!");
                                    inserted = true;
                                    break;
                                }
                            }
                            break;

                        case "ADD":
                            for (int z = 0; z < fpAdderRS; z++) {
                                //insert if there is a blank space
                                if (!inserted && (addArr[z] == blank || addArr[z] == null)) {
                                    addArr[z] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                                    inserted = true;
                                    //System.out.println("Add issued!");
                                    break;
                                }
                            }
                            break;

                        case "MUL":
                            for (int z = 0; z < fpMultiplierRS; z++) {
                                //insert if there is a blank space
                                if (!inserted && (mulArr[z] == blank || mulArr[z] == null)) {
                                    mulArr[z] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                                    //System.out.println("Multiply issued!");
                                    inserted = true;
                                    break;
                                }
                            }
                            break;

                        case "DIV":
                            for (int z = 0; z < fpDividerRS; z++) {
                                //insert if there is a blank space
                                if (!inserted && (divArr[z] == blank || divArr[z] == null)) {
                                    divArr[z] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                                    //System.out.println("Divide issued!");
                                    inserted = true;
                                    break;
                                }
                            }
                            break;

                        case "BRA":
                            //---WIP---
                            break;

                        case "NOP":
                            //do nothing
                            break;

                        case "HALT":
                            //stop prgm
                            break;
                    }
                }

                //Place in awaiting RS/registers
                else if (MainLogic.OperationInfoStation.get(i).state.equals("WB")) {
                    System.out.println("Instruction is in WB");
                    switch (MainLogic.OperationInfoStation.get(i).operand) {
                        //remove element from corresponding RS
                        case "LOAD":
                            //System.out.println("Removing load");
                            for(int l = 0; l < ldBuffer; l++){
                                if(ldArr[l] != blank && "WB".equals(ldArr[l].state)){
                                    ldArr[l] = blank; //remove from buffer and replace with blank instr
                                 break;
                                }
                            }
                            break;

                        case "SAVE":

                            for(int l = 0; l < sdBuffer; l++){
                                if("WB".equals(sdArr[l].state)){
                                    sdArr[l] = blank; //remove from buffer and replace with blank instr
                                    break;
                                }
                            }
                            break;

                        case "INT":
                            for(int l = 0; l < integerRS; l++){
                                if("WB".equals(intArr[l].state)){
                                    intArr[l] = blank; //remove from buffer and replace with blank instr
                                    break;
                                }
                            }
                            break;

                        case "ADD":
                            for(int l = 0; l < fpAdderRS; l++){
                                if("WB".equals(addArr[l].state)){
                                    addArr[l] = blank; //remove from buffer and replace with blank instr
                                    break;
                                }
                            }
                            break;

                        case "MUL":
                            for(int l = 0; l < fpMultiplierRS; l++){
                                if("WB".equals(mulArr[l].state)){
                                    mulArr[l] = blank; //remove from buffer and replace with blank instr
                                    break;
                                }
                            }
                            break;

                        case "DIV":
                            for(int l = 0; l < fpDividerRS; l++){
                                if("WB".equals(divArr[l].state)){
                                    divArr[l] = blank; //remove from buffer and replace with blank instr
                                    break;
                                }
                            }
                            break;

                        case "BRA":
                    }
                    for (int r = 0; r < registers; r++) {
                        if (!MainLogic.OperationInfoStation.get(i).state.equals("SAVE") && (regArr[r] == blank || regArr[r] == null)) {
                            regArr[r] = new Instruction(MainLogic.OperationInfoStation.get(i).operand, MainLogic.OperationInfoStation.get(i).DestReg, MainLogic.OperationInfoStation.get(i).SourceReg1, MainLogic.OperationInfoStation.get(i).SourceReg2, MainLogic.OperationInfoStation.get(i).state, MainLogic.OperationInfoStation.get(i).currentStageCycleNum, MainLogic.OperationInfoStation.get(i).exeStart);
                            System.out.println("Writing to register");
                            break;
                        }
                    }
                }
            }
            tick++;
        }


        //---Connecting Wires---
        g.setColor((Color.decode(DataUI.colorSchemeMainCur[6])));
        g.setStroke(new BasicStroke(3));

        g.drawString("Common Data Bus", originX - 400, originY + 120);
        g.setColor(Color.decode(DataUI.colorSchemeCycleCur[0]));
        drawThickLine(g, originX + ldBase[0] + 25, originY + ldBase[1] + 2, originX + ldBase[0] + 25, originY + 105);
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

    public void flushBuffers(){
        Arrays.fill(opQArr, blank);
        Arrays.fill(ldArr, blank);
        Arrays.fill(sdArr, blank);
        Arrays.fill(intArr, blank);
        Arrays.fill(addArr, blank);
        Arrays.fill(mulArr, blank);
        Arrays.fill(divArr, blank);
        Arrays.fill(regArr, blank);
        Arrays.fill(opQ, "");
    }

    public void updateState(){
        for (int z = 0; z < ldBuffer; z++) {
            //Keep track of exeTime that has passed for the things in the buffers
            if (ldArr[z] != blank || ldArr[z] != null) {
                //System.out.println(ldArr[z].currentClk);
                if (ldArr[z].currentClk <= MainLogic.CycleNumCur - ldArr[z].startTime) {
                    ldArr[z].state = "WB";
                    //System.out.println("State " + ldArr[z].state);
                    //break;
                }
            }
        }

        for (int z = 0; z < sdBuffer; z++) {
            //Keep track of exeTime that has passed for the things in the buffers
            if (sdArr[z] != blank || sdArr[z] != null) {
                //System.out.println(ldArr[z].currentClk);
                if (sdArr[z].currentClk <= MainLogic.CycleNumCur - sdArr[z].startTime) {
                    sdArr[z].state = "WB";
                    //System.out.println("State " + ldArr[z].state);
                }
            }
        }

        for (int z = 0; z < integerRS; z++) {
            //Keep track of exeTime that has passed for the things in the buffers
            if (intArr[z] != blank || intArr[z] != null) {
                //System.out.println(ldArr[z].currentClk);
                if (intArr[z].currentClk <= MainLogic.CycleNumCur - intArr[z].startTime) {
                    intArr[z].state = "WB";
                    //System.out.println("State " + ldArr[z].state);
                }
            }
        }
    }
}

/*
Instruction[] opQArr = new Instruction[MainLogic.OpQueue];
    Instruction[] ldArr = new Instruction[ldBuffer];
    Instruction[] sdArr = new Instruction[sdBuffer];
    Instruction[] intArr = new Instruction[integerRS];
    Instruction[] addArr = new Instruction[fpAdderRS];
    Instruction[] mulArr = new Instruction[fpMultiplierRS];
    Instruction[] divArr = new Instruction[fpDividerRS];
    Instruction[] regArr = new Instruction[registers];
 */
