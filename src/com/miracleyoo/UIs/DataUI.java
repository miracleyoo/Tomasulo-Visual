package com.miracleyoo.UIs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import com.miracleyoo.utils.*;

public class DataUI {
    private JPanel PanelMain;
    private JButton ExecuteOneStepBtn;
    private JButton ExecuteMultipleStepBtn;
    private JTable OperandTable;
    private final int[] slice = {0,5};

    // Column names for the OperandTable
    private static String[] columnNames;

    // Total data for the OperandTable
    static private Object[][] fullData,rawData;

    private void update() {
        if(slice[0]>=fullData.length-1){
            rawData = null; //new Object[][]{{"", "", "", "", ""}};
        }
        else {
            rawData = Arrays.copyOfRange(fullData, slice[0], slice[1]);
        }
        TableModel dataModel = new DefaultTableModel(rawData,columnNames);
        OperandTable.setModel(dataModel);
    }

    DataUI(Object[][] inputTotalData, String[] inputColumnNames) {
        // Configure the table
        fullData = inputTotalData;
        columnNames = inputColumnNames;
        rawData = Arrays.copyOfRange(fullData, slice[0], slice[1]);
        TableModel dataModel = new DefaultTableModel(rawData, columnNames);

        OperandTable.setModel(dataModel);
//        OperandTable.setPreferredScrollableViewportSize(new Dimension(200, 30));
        OperandTable.setFillsViewportHeight(true);

        // Execute one step button action
        ExecuteOneStepBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slice[0]++;
                slice[1]++;
                update();
            }
        });

        // Execute multiple step button action
        ExecuteMultipleStepBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slice[0]+=3;
                slice[1]+=3;
                update();
            }
        });

        JFrame frame = new JFrame("Operands");
        frame.setContentPane(PanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UICommonUtils.makeFrameToCenter(frame);
        frame.setVisible(true);
    }
}
