package com.miracleyoo.UIs;

import javax.swing.*;
import java.awt.*;

public class Diagram extends JPanel {

    int fontSize = 9;

    //Standardize block widths/heights
    public static int height = 12;
    static int  opBoxWidth = 30;
    static int operandWidth = 50;

    //Designate number of RS per Functional unit here
    int ldBuffer = (int) DataUI.architectureNum[0];
    int sdBuffer = (int) DataUI.architectureNum[1];
    int integerRS = (int) DataUI.architectureNum[2];
    int fpAdderRS = (int) DataUI.architectureNum[3];
    int fpMultiplierRS = (int) DataUI.architectureNum[4];
    int fpDividerRS = (int) DataUI.architectureNum[5];
    public static int OpQueue = 10; //Sharing opQueue for int and fp
    static int registers = 10;
    public static int diagramWidth = 600 + 200 + 50 + 50; // Most left: -200; Most right: 200 + 50(rect width)
    public static int diagramHeight = 110 + height * OpQueue + height + 30; // Most down: -110; Most top: Reg rects top
//    this.setSize(diagramWidth, diagramHeight);

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diagramWidth, diagramHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        setSize(diagramWidth, diagramHeight);

        //Allows for window scaling while keeping objects in their relative positions
        int originX = getWidth() / 2 - 25;// getViewport().getSize().width;// getWidth()/2;
        int originY = getHeight() / 2 + 35; //getViewport().getSize().height;//getHeight()/2;

        //print to console the number of reservation stations
        for(int i = 0; i < 6; i++) {
            System.out.println(DataUI.architectureNum[i]);
        }

        //Place ldBuffers
        int[] ldBase = {-400, -60};
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        g.drawString("LD Buffer (From Memory)", originX + ldBase[0], originY - (height * ldBuffer + height) + ldBase[1]);
        for (int i = 0; i < ldBuffer; i++) {
            g.drawRect(originX +ldBase[0], originY - (height * i + height) + ldBase[1], 50, height);
        }

        g.setColor(Color.BLACK);

        //Place sdBuffers
        int[] sdBase = {250, -60};
        g.drawString("SD Buffer (To Memory)", originX + sdBase[0], originY - (height * sdBuffer + height) + sdBase[1]);
        for (int i = 0; i < sdBuffer; i++) {
            g.drawRect(originX + sdBase[0], originY - (height * i + height) + sdBase[1], 50, height);
        }
        //g.setColor((Color.RED));
        //g.drawLine(originX + sdBase[0] + 25, originY + sdBase[1], originX + sdBase[0] + 25, originY + 100);
        g.setColor(Color.BLACK);

        //Place OpQueue -- Note Op Queue is currently implementing both int and fp, THIS MAY NEED TO CHANGE!
        g.drawString("OP Queue", originX - 100, originY - (height * OpQueue + height) - 60);
        for (int q = 0; q < OpQueue; q++) {
            g.drawRect(originX - 100, originY - (height * q + height) - 60, 80, height);
        }

        g.drawString("Int/FP Registers", originX + 50, originY - (height * registers + height) - 60);
        for (int q = 0; q < registers; q++) {
            g.drawRect(originX + 50, originY - (height * q + height) - 60, 80, height);
        }


        //Place integer FU
        int intBase[] = {-300, 60}; //x, y
        for(int a = 0; a < integerRS; a++){
            g.setColor(Color.BLACK);
            g.drawRect(originX - opBoxWidth + intBase[0], originY - (height * a) + intBase[1], opBoxWidth, height);
            g.drawRect(originX + intBase[0], originY - (height * a) + intBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + intBase[0], originY - (height * a) + intBase[1], operandWidth, height);
            g.drawString("IntegerFU", originX + intBase[0] + 5, originY + intBase[1] + 30);
            g.drawRect(originX + intBase[0], originY + intBase[1] + 20, 80, height);
            g.setColor(Color.RED);
            g.drawLine(originX + intBase[0] + 40, originY + intBase[1] + 30, originX + intBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[] {originX + intBase[0] + 35, originX + intBase[0] + 40, originX + intBase[0] + 45}, new int[] {originY + 95, originY + 100, originY + 95}, 3);
            g.drawLine(originX + intBase[0] + 90, originY + intBase[1] + 10, originX + intBase[0] + 90, originY + 100); //From CDB
            g.fillPolygon(new int[] {originX + intBase[0] + 85, originX + intBase[0] + 90, originX + intBase[0] + 95}, new int[] {originY + intBase[1] + 20, originY + intBase[1] + 10, originY + intBase[1] + 20}, 3);
        }

        //Place fp adder FU
        int addBase[] = {-150, 60}; //used to set origin of adder FU on Tomasulo graph. X and then Y.
        //g.drawString("FPadder", originX + addBase[0], originY + addBase[1]);
        for (int x = 0; x < fpAdderRS; x++) {
            g.setColor(Color.black);
            g.drawRect(originX - opBoxWidth + addBase[0], originY - (height * x) + addBase[1], opBoxWidth, height);
            g.drawRect(originX + addBase[0], originY - (height * x) + addBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + addBase[0], originY - (height * x) + addBase[1], operandWidth, height);
            g.drawString("FPadder", originX + addBase[0] + 5, originY + addBase[1] + 30);
            g.drawRect(originX + addBase[0], originY + addBase[1] + 20, 80, height);
            g.setColor(Color.RED);
            g.drawLine(originX + addBase[0] + 40, originY + addBase[1] + 30, originX + addBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[] {originX + addBase[0] + 35, originX + addBase[0] + 40, originX + addBase[0] + 45}, new int[] {originY + 95, originY + 100, originY + 95}, 3);
            g.drawLine(originX + addBase[0] + 90, originY + addBase[1] + 10, originX + addBase[0] + 90, originY + 100); //from CDB
            g.fillPolygon(new int[] {originX + addBase[0] + 85, originX + addBase[0] + 90, originX + addBase[0] + 95}, new int[] {originY + addBase[1] + 20, originY + addBase[1] + 10, originY + addBase[1] + 20}, 3);
        }

        //fp multiplier FU
        int mulBase[] = {0, 60}; //used to set origin of multiplier FU on Tomasulo graph
        //g.drawString("FPmultiplier", originX + mulBase[0], originY + mulBase[1]);
        for (int y = 0; y < fpMultiplierRS; y++) {
            g.setColor(Color.black);
            g.drawRect(originX - opBoxWidth + mulBase[0], originY - (height * y) + mulBase[1], opBoxWidth, height);
            g.drawRect(originX + mulBase[0], originY - (height * y) + mulBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + mulBase[0], originY - (height * y) + mulBase[1], operandWidth, height);
            g.drawString("FPmult", originX + mulBase[0] + 5, originY + mulBase[1] + 30);
            g.drawRect(originX + mulBase[0], originY + mulBase[1] + 20, 80, height);
            g.setColor(Color.RED);
            g.drawLine(originX + mulBase[0] + 40, originY + mulBase[1] + 30, originX + mulBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[] {originX + mulBase[0] + 35, originX + mulBase[0] + 40, originX + mulBase[0] + 45}, new int[] {originY + 95, originY + 100, originY + 95}, 3);
            g.drawLine(originX + mulBase[0] + 90, originY + mulBase[1] + 10, originX + mulBase[0] + 90, originY + 100); //from CDB
            g.fillPolygon(new int[] {originX + mulBase[0] + 85, originX + mulBase[0] + 90, originX + mulBase[0] + 95}, new int[] {originY + mulBase[1] + 20, originY + mulBase[1] + 10, originY + mulBase[1] + 20}, 3);
        }

        //fp Div FU
        int divBase[] = {150, 60};
        //g.drawString("Divider", 190, 80);
        for (int z = 0; z < fpDividerRS; z++) {
            g.setColor(Color.black);
            g.drawRect(originX - opBoxWidth + divBase[0], originY - (height * z) + divBase[1], opBoxWidth, height);
            g.drawRect(originX + divBase[0], originY - (height * z) + divBase[1], operandWidth, height);
            g.drawRect(originX + operandWidth + divBase[0], originY - (height * z) + divBase[1], operandWidth, height);
            g.drawString("FPdiv", originX + divBase[0] + 5, originY + divBase[1] + 30);
            g.drawRect(originX + divBase[0], originY + divBase[1] + 20, 80, height);
            g.setColor(Color.RED);
            g.drawLine(originX + divBase[0] + 40, originY + divBase[1] + 30, originX + divBase[0] + 40, originY + 100); //to CDB
            g.fillPolygon(new int[] {originX + divBase[0] + 35, originX + divBase[0] + 40, originX + divBase[0] + 45}, new int[] {originY + 95, originY + 100, originY + 95}, 3);
            g.drawLine(originX + divBase[0] + 90, originY + divBase[1] + 10, originX + divBase[0] + 90, originY + 100); //from CDB
            g.fillPolygon(new int[] {originX + divBase[0] + 85, originX + divBase[0] + 90, originX + divBase[0] + 95}, new int[] {originY + divBase[1] + 20, originY + divBase[1] + 10, originY + divBase[1] + 20}, 3);

        }

        //---Connecting Wires---
        g.setColor((Color.black));
        g.drawString("Common Data Bus", originX - 400, originY + 110);
        g.setColor(Color.RED);
        g.drawLine(originX + ldBase[0] + 25, originY + ldBase[1], originX + ldBase[0] + 25, originY + 100);
        g.fillPolygon(new int[] {originX + ldBase[0] + 20 , originX + ldBase[0] + 25 , originX + ldBase[0] + 30}, new int[] {originY + 90, originY + 100, originY + 90}, 3);
        g.drawLine(originX + ldBase[0],originY + 100, originX + sdBase[0] + 100, originY + 100); //CBD horizontal line
        g.drawLine(originX + sdBase[0] + 100, originY + sdBase[1] - registers*height + 5, originX + sdBase[0] + 100, originY + 100); //CBD vertical line going up to store data
        g.drawLine(originX + sdBase[0] + 50, originY + sdBase[1] - sdBuffer*height + 5, originX + sdBase[0] + 100, originY + sdBase[1] - sdBuffer*height + 5); //Horizontal line to SD buffer
        g.fillPolygon(new int[] {originX + sdBase[0] + 50, originX + sdBase[0] + 60, originX + sdBase[0] + 60}, new int[] {originY + sdBase[1] - sdBuffer*height + 5, originY + sdBase[1] - sdBuffer*height + 10, originY + sdBase[1] - sdBuffer*height}, 3);
        g.drawLine(originX + 50 + 80 , originY + sdBase[1] - registers*height + 5, originX + sdBase[0] + 100, originY + sdBase[1] - registers*height + 5); //Horizontal line connecting to Registers
        g.fillPolygon(new int[] {originX + 50 + 80, originX + 50 + 90, originX + 50 + 90}, new int[] {originY + sdBase[1] - registers*height + 5, originY + sdBase[1] - registers*height + 10, originY + sdBase[1] - registers*height}, 3);

        //OpQueue Wires
        g.setColor(Color.BLUE);
        g.drawLine(originX - 100 + 40, originY - 60, originX - 100 + 40, originY - 40); //Vertical line connecting Op-Queue
        g.drawLine(originX - opBoxWidth + intBase[0] + 15, originY + intBase[1] - (integerRS - 1)*height, originX - opBoxWidth + intBase[0] + 15, originY - 40); //Vertical line connecting intOp
        g.fillPolygon(new int[] {originX - opBoxWidth + intBase[0] + 10, originX - opBoxWidth + intBase[0] + 15, originX - opBoxWidth + intBase[0] + 20}, new int[] {originY + intBase[1] - (integerRS - 1)*height - 5, originY + intBase[1] - (integerRS - 1)*height, originY + intBase[1] - (integerRS - 1)*height - 5}, 3);
        g.drawLine(originX - opBoxWidth + addBase[0] + 15, originY + addBase[1] - (fpAdderRS - 1)*height, originX - opBoxWidth + addBase[0] + 15, originY - 40); //Vertical line connecting fpAddOp
        g.fillPolygon(new int[] {originX - opBoxWidth + addBase[0] + 10, originX - opBoxWidth + addBase[0] + 15, originX - opBoxWidth + addBase[0] + 20}, new int[] {originY + addBase[1] - (fpAdderRS - 1)*height - 5, originY + addBase[1] - (fpAdderRS - 1)*height, originY + addBase[1] - (fpAdderRS - 1)*height - 5}, 3);
        g.drawLine(originX - opBoxWidth + mulBase[0] + 15, originY + mulBase[1] - (fpMultiplierRS - 1)*height, originX - opBoxWidth + mulBase[0] + 15, originY - 40); //Vertical line connecting fpMulOp
        g.fillPolygon(new int[] {originX - opBoxWidth + mulBase[0] + 10, originX - opBoxWidth + mulBase[0] + 15, originX - opBoxWidth + mulBase[0] + 20}, new int[] {originY + mulBase[1] - (fpMultiplierRS - 1)*height - 5, originY + mulBase[1] - (fpMultiplierRS - 1)*height, originY + mulBase[1] - (fpMultiplierRS - 1)*height - 5}, 3);
        g.drawLine(originX - opBoxWidth + divBase[0] + 15, originY + divBase[1] - (fpDividerRS - 1)*height, originX - opBoxWidth + divBase[0] + 15, originY - 40); //Vertical line connecting fpDivOp
        g.fillPolygon(new int[] {originX - opBoxWidth + divBase[0] + 10, originX - opBoxWidth + divBase[0] + 15, originX - opBoxWidth + divBase[0] + 20}, new int[] {originY + divBase[1] - (fpDividerRS - 1)*height - 5, originY + divBase[1] - (fpDividerRS - 1)*height, originY + divBase[1] - (fpDividerRS - 1)*height - 5}, 3);
        g.drawLine(originX - opBoxWidth + intBase[0] + 15, originY - 40, originX - opBoxWidth + divBase[0] + 15, originY - 40); //Horizontal connecting line

        //src1 line
        g.setColor(Color.MAGENTA);
        g.drawLine(originX + 50 + 10, originY - 60, originX + 50 + 10, originY - 30); //Vertical line from register connecting to src1
        g.drawLine(originX + intBase[0] + 25, originY + intBase[1] - (integerRS - 1)*height, originX + intBase[0] + 25, originY - 30); //Vertical line for intRS src1
        g.fillPolygon(new int[] {originX + intBase[0] + 20, originX + intBase[0] + 25, originX + intBase[0] + 30}, new int[] {originY + intBase[1] - (integerRS - 1)*height - 5, originY + intBase[1] - (integerRS - 1)*height, originY + intBase[1] - (integerRS - 1)*height - 5}, 3);
        g.drawLine(originX + addBase[0] + 25, originY + addBase[1] - (fpAdderRS - 1)*height, originX + addBase[0] + 25, originY - 30); //Vertical line for fpAdderRS src1
        g.fillPolygon(new int[] {originX + addBase[0] + 20, originX + addBase[0] + 25, originX + addBase[0] + 30}, new int[] {originY + addBase[1] - (fpAdderRS - 1)*height - 5, originY + addBase[1] - (fpAdderRS - 1)*height, originY + addBase[1] - (fpAdderRS - 1)*height - 5}, 3);
        g.drawLine(originX + mulBase[0] + 25, originY + mulBase[1] - (fpMultiplierRS - 1)*height, originX + mulBase[0] + 25, originY - 30); //Vertical line for fpMulRS src1
        g.fillPolygon(new int[] {originX + mulBase[0] + 20, originX + mulBase[0] + 25, originX + mulBase[0] + 30}, new int[] {originY + mulBase[1] - (fpMultiplierRS - 1)*height - 5, originY + mulBase[1] - (fpMultiplierRS - 1)*height, originY + mulBase[1] - (fpMultiplierRS - 1)*height - 5}, 3);
        g.drawLine(originX + divBase[0] + 25, originY + divBase[1] - (fpDividerRS - 1)*height, originX + divBase[0] + 25, originY - 30); //Vertical line for fpDivRS src1
        g.fillPolygon(new int[] {originX + divBase[0] + 20, originX + divBase[0] + 25, originX + divBase[0] + 30}, new int[] {originY + divBase[1] - (fpDividerRS - 1)*height - 5, originY + divBase[1] - (fpDividerRS - 1)*height, originY + divBase[1] - (fpDividerRS - 1)*height - 5}, 3);
        g.drawLine(originX + intBase[0] + 25, originY - 30, originX + divBase[0] + 25, originY - 30); //Horizontal connecting line


        //src2 line
        g.setColor(Color.ORANGE);
        g.drawLine(originX + 50 + 70, originY - 60, originX + 50 + 70, originY - 50); //Vertical line from register connecting to src2
        g.drawLine(originX + operandWidth + intBase[0] + 25, originY + intBase[1] - (integerRS - 1)*height, originX + operandWidth + intBase[0] + 25, originY - 50); //Vertical line for intRS src2
        g.fillPolygon(new int[] {originX + operandWidth + intBase[0] + 20, originX + operandWidth + intBase[0] + 25, originX + operandWidth + intBase[0] + 30}, new int[] {originY + intBase[1] - (integerRS - 1)*height - 5, originY + intBase[1] - (integerRS - 1)*height, originY + intBase[1] - (integerRS - 1)*height - 5}, 3);
        g.drawLine(originX + operandWidth + addBase[0] + 25, originY + addBase[1] - (fpAdderRS - 1)*height, originX + operandWidth + addBase[0] + 25, originY - 50); //Vertical line for fpAdderRS src2
        g.fillPolygon(new int[] {originX + operandWidth + addBase[0] + 20, originX + operandWidth + addBase[0] + 25, originX + operandWidth + addBase[0] + 30}, new int[] {originY + addBase[1] - (fpAdderRS - 1)*height - 5, originY + addBase[1] - (fpAdderRS - 1)*height, originY + addBase[1] - (fpAdderRS - 1)*height - 5}, 3);
        g.drawLine(originX + operandWidth + mulBase[0] + 25, originY + mulBase[1] - (fpMultiplierRS - 1)*height, originX + operandWidth + mulBase[0] + 25, originY - 50); //Vertical line for fpMulRS src2
        g.fillPolygon(new int[] {originX + operandWidth + mulBase[0] + 20, originX + operandWidth + mulBase[0] + 25, originX + operandWidth + mulBase[0] + 30}, new int[] {originY + mulBase[1] - (fpMultiplierRS - 1)*height - 5, originY + mulBase[1] - (fpMultiplierRS - 1)*height, originY + mulBase[1] - (fpMultiplierRS - 1)*height - 5}, 3);
        g.drawLine(originX + operandWidth + divBase[0] + 25, originY + divBase[1] - (fpDividerRS - 1)*height, originX + operandWidth + divBase[0] + 25, originY - 50); //Vertical line for fpDivRS src2
        g.fillPolygon(new int[] {originX + operandWidth + divBase[0] + 20, originX + operandWidth + divBase[0] + 25, originX + operandWidth + divBase[0] + 30}, new int[] {originY + divBase[1] - (fpDividerRS - 1)*height - 5, originY + divBase[1] - (fpDividerRS - 1)*height, originY + divBase[1] - (fpDividerRS - 1)*height - 5}, 3);
        g.drawLine(originX + operandWidth + intBase[0] + 25, originY - 50, originX + operandWidth + divBase[0] + 25, originY - 50); //Horizontal connecting line



        repaint();
    }
}
