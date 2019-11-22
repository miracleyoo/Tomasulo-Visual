package com.miracleyoo.UIs;

import javax.swing.*;
import java.awt.*;

public class Diagram extends JPanel {

    int fontSize = 9;

    //Standardize block widths/heights
    public static int height = 10;
    int opBoxWidth = 30;
    int operandWidth = 50;

    //Designate number of RS per Functional unit here
    int ldBuffer = (int) DataUI.architectureNum[0];
    int sdBuffer = (int) DataUI.architectureNum[1];
    int integerRS = (int) DataUI.architectureNum[2];
    int fpAdderRS = (int) DataUI.architectureNum[3];
    int fpMultiplierRS = (int) DataUI.architectureNum[4];
    int fpDividerRS = (int) DataUI.architectureNum[5];
    public static int OpQueue = 10; //Sharing opQueue for int and fp
    int registers = 10;
    //int fpRegisters = 10;
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
        int originX = getWidth() / 2-25;// getViewport().getSize().width;// getWidth()/2;
        int originY = getHeight() / 2; //getViewport().getSize().height;//getHeight()/2;


        //Place ldBuffers
        int[] ldBase = {-400, -40};
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        g.drawString("LD Buffer", originX + ldBase[0], originY - (height * ldBuffer + height) + ldBase[1]);
        for (int i = 0; i < ldBuffer; i++) {
            g.drawRect(originX +ldBase[0], originY - (height * i + height) + ldBase[1], 50, height);
        }
        g.setColor((Color.RED));
        g.drawLine(originX + ldBase[0] + 25, originY + ldBase[1], originX + ldBase[0] + 25, originY + 100);
        g.fillPolygon(new int[] {originX + ldBase[0] + 20 , originX + ldBase[0] + 25 , originX + ldBase[0] + 30}, new int[] {originY + 90, originY + 100, originY + 90}, 3);
        g.setColor(Color.BLACK);

        //Place sdBuffers
        int[] sdBase = {220, -40};
        g.drawString("SD Buffer", originX + sdBase[0], originY - (height * sdBuffer + height) + sdBase[1]);
        for (int i = 0; i < sdBuffer; i++) {
            g.drawRect(originX + sdBase[0], originY - (height * i + height) + sdBase[1], 50, height);
        }
        //g.setColor((Color.RED));
        //g.drawLine(originX + sdBase[0] + 25, originY + sdBase[1], originX + sdBase[0] + 25, originY + 100);
        g.setColor(Color.BLACK);

        //Place OpQueue -- Note Op Queue is currently implmenting both int and fp, THIS MAY NEED TO CHANGE!
        g.drawString("OP Queue", originX - 100, originY - (height * OpQueue + height));
        for (int q = 0; q < OpQueue; q++) {
            g.drawRect(originX - 100, originY - (height * q + height), 80, height);
        }

        g.drawString("Int/FP Registers", originX + 50, originY - (height * registers + height));
        for (int q = 0; q < registers; q++) {
            g.drawRect(originX + 50, originY - (height * q + height), 80, height);
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
            g.drawLine(originX + intBase[0] + 40, originY + intBase[1] + 30, originX + intBase[0] + 40, originY + 100);
            g.drawLine(originX + intBase[0] + 90, originY + intBase[1] + 10, originX + intBase[0] + 90, originY + 100);
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
            g.drawLine(originX + addBase[0] + 40, originY + addBase[1] + 30, originX + addBase[0] + 40, originY + 100);
            g.drawLine(originX + addBase[0] + 90, originY + addBase[1] + 10, originX + addBase[0] + 90, originY + 100);
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
            g.drawLine(originX + mulBase[0] + 40, originY + mulBase[1] + 30, originX + mulBase[0] + 40, originY + 100);
            g.drawLine(originX + mulBase[0] + 90, originY + mulBase[1] + 10, originX + mulBase[0] + 90, originY + 100);
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
            g.drawLine(originX + divBase[0] + 40, originY + divBase[1] + 30, originX + divBase[0] + 40, originY + 100);
            g.drawLine(originX + divBase[0] + 90, originY + divBase[1] + 10, originX + divBase[0] + 90, originY + 100);
        }

        //---Connecting Wires---
        g.setColor((Color.black));
        g.drawString("Common Data Bus", originX - 400, originY + 110);
        g.setColor(Color.RED);
        g.drawLine(originX + ldBase[0] + 25,originY + 100, originX + sdBase[0] + 60, originY + 100); //CBD horizontal line
        g.drawLine(originX + sdBase[0] + 60, originY + sdBase[1] - sdBuffer*height + 5, originX + sdBase[0] + 60, originY + 100); //CBD line going up to store data
        g.drawLine(originX + sdBase[0] + 50, originY + sdBase[1] - sdBuffer*height + 5, originX + sdBase[0] + 60, originY + sdBase[1] - sdBuffer*height + 5);



        repaint();
    }
}
