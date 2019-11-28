package com.miracleyoo.UIs;

import com.miracleyoo.utils.Instruction;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;

public class Diagram extends JPanel {

    int cycleNum; //To keep track of cycle number
    int cycleNumOld = 0;

    Instruction blank = new Instruction("", "", "", "", 0);
    Instruction[] opQArr = new Instruction[10];

    //example instructions
    String[] instr = {"lw", "sw", "lw", "FPadd", "FPmul", "FPdiv", "sw", "ld", "INTadd", "INTsub", "FPsub", "sw", "INTadd", "INTmul", "FPdiv"};
    int instrIndex = 0;

    int fontSize = 9;

    //Standardize block widths/heights
    public static int height = 12;
    static int opBoxWidth = 30;
    static int operandWidth = 50;

    //Designate number of RS per Functional unit here
    int sdBuffer = (int) DataUI.architectureNum[0];
    int ldBuffer = (int) DataUI.architectureNum[1];
    int integerRS = (int) DataUI.architectureNum[2];
    int fpAdderRS = (int) DataUI.architectureNum[3];
    int fpMultiplierRS = (int) DataUI.architectureNum[4];
    int fpDividerRS = (int) DataUI.architectureNum[5];
    public static int OpQueue = 10; //Sharing opQueue for int and fp
    static int registers = 10;
    public static int diagramWidth = 600 + 200 + 50 + 50; // Most left: -200; Most right: 200 + 50(rect width)
    public static int diagramHeight = 110 + height * OpQueue + height + 30; // Most down: -110; Most top: Reg rects top
    //Allows for window scaling while keeping objects in their relative positions


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diagramWidth, diagramHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        paintComponent((Graphics2D) g);
    }

    private void drawThickLine(Graphics2D g, int x1, int y1, int x2, int y2){
        g.setStroke(new BasicStroke(3));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(new BasicStroke(1));
    }

    public void setCycleNum(int c){
        cycleNum = c;
    }

    protected void paintComponent(Graphics2D g){
        super.paintComponent(g);
        g.setColor(Color.decode(DataUI.colorSchemeMainCur[6]));
        int originX = getWidth() / 2 - 25;// getViewport().getSize().width;// getWidth()/2;
        int originY = getHeight() / 2 + 35; //getViewport().getSize().height;//getHeight()/2;
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));


        g.drawString(Integer.toString(cycleNum), originX, originY); //debugging to keep track of updating diagram in sync with cycleNum

        //Place OpQueue -- Note Op Queue is currently implementing both int and fp, THIS MAY NEED TO CHANGE!
        g.drawString("OP Queue", originX - 100, originY - (height * OpQueue + height) - 60);
        for (int q = 0; q < OpQueue; q++) {
            g.drawRect(originX - 100, originY - (height * q + height) - 60, 80, height);
        }

        //Push instructions to opQueue --> Instructions stored here until ISSUED to prevent structural hazard
        //Shift instructions down as they are processed through the OpQueue in FIFO manner. In order issue one instruction at a time!


        //Push instructions onto opQArr initially
        if(instrIndex < instr.length) { //needs to be adjusted to allow all instructions in OpQueue to be pushed through pipeline before throwing no more instr msg.
            for (int q = 0; q < OpQueue; q++) {
                //if opQArr has a blank position, push next awaiting instruction
                if(opQArr[q] == null) {
                    opQArr[q] = new Instruction(instr[instrIndex], "", "", "", 1);
                    System.out.println("Instruction added: " + opQArr[q].op);
                    instrIndex++;
                }
            }
        }

        else {
            g.drawString("No more instructions left!", originX - 200, originY - 150);
        }

        //Try to push next instruction every clock cycle
        if(cycleNum != cycleNumOld){
            //need to shift all elements in opQArr down by 1 index
            for(int q = 0; q < OpQueue-1; q++){
                opQArr[q] = opQArr[q+1];
            }
            //Check if instr array has awaiting instr.
            if(instrIndex < instr.length) {
                opQArr[OpQueue - 1] = new Instruction(instr[instrIndex], "", "", "", 0); //Grab next instruction from instruction array
                instrIndex++;
            }

            //If no more instructions, begin clearing the Opqueue
            else{
                opQArr[OpQueue - 1] = blank;
            }

            for(int q = 0; q < OpQueue; q++) {
                System.out.println("Instruction added: " + opQArr[q].op);
            }

            cycleNumOld = cycleNum;
        }

        for(int q = 0; q < OpQueue; q++){
            g.drawString(opQArr[q].op, originX - 95, originY - (height * q) - 62);
        }


        //ldBuffers
        int[] ldBase = {-400, -60};
        g.drawString("LD Buffer (From Memory)", originX + ldBase[0], originY - (height * ldBuffer + height) + ldBase[1]);
        for (int i = 0; i < ldBuffer; i++) {
            g.drawRect(originX + ldBase[0], originY - (height * i + height) + ldBase[1], 50, height);
        }

        //if OpQArr[0] is lw
        //Create ldWord array to hold instructions while they execute load
        String[] ldArr = new String[ldBuffer]; //ldArray can hold at most capacity of ldBuffer
        if(opQArr[0].op.equals("lw")){
            //System.out.println("load true!");
            ldArr[0] = opQArr[0].op;
            g.drawString(ldArr[0], originX - 5, originY - (height * 1) + ldBase[0] - 3);
        }

        //for adding more lw to the lwBuffer
        /*
        for(int l = 0; l < DataUI.architectureNum[0]; l++){

        }
         */



        //Place sdBuffers
        int[] sdBase = {250, -60};
        g.drawString("SD Buffer (To Memory)", originX + sdBase[0], originY - (height * sdBuffer + height) + sdBase[1]);
        for (int i = 0; i < sdBuffer; i++) {
            g.drawRect(originX + sdBase[0], originY - (height * i + height) + sdBase[1], 50, height);
        }


        g.drawString("Int/FP Registers", originX + 50, originY - (height * registers + height) - 60);
        for (int q = 0; q < registers; q++) {
            g.drawRect(originX + 50, originY - (height * q + height) - 60, 80, height);
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

        //Place fp adder FU
        int addBase[] = {-150, 60}; //used to set origin of adder FU on Tomasulo graph. X and then Y.
        //g.drawString("FPadder", originX + addBase[0], originY + addBase[1]);
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

        //fp multiplier FU
        int mulBase[] = {0, 60}; //used to set origin of multiplier FU on Tomasulo graph
        //g.drawString("FPmultiplier", originX + mulBase[0], originY + mulBase[1]);
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

        //fp Div FU
        int divBase[] = {150, 60};
        //g.drawString("Divider", 190, 80);
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


}
