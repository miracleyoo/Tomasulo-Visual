package com.miracleyoo.UIs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    // Total data for the OperandTable
    static private Object[][] fullData,rawData;
    static private String[] columnNames;
    static final int[] columnWidths = new int[]{10, 300};
    private void update(DefaultTableModel dataModel) {
        if(slice[0]>=fullData.length-1){
            rawData = null;
        }
        else {
            rawData = Arrays.copyOfRange(fullData, slice[0], slice[1]);
        }
        dataModel.setDataVector(rawData, columnNames);
        dataModel.fireTableDataChanged();
        TableUtils.setAllMinColumnSize(OperandTable, columnWidths);
        TableUtils.setAllPreferredColumnSize(OperandTable, columnWidths);
    }

    DataUI(Object[][] inputTotalData, String[] inputColumnNames) {
        // Configure the table
        fullData = inputTotalData;
        // Column names for the OperandTable
        rawData = Arrays.copyOfRange(fullData, slice[0], slice[1]);
        columnNames = inputColumnNames;
        DefaultTableModel dataModel = new DefaultTableModel(rawData, columnNames);

        OperandTable.setModel(dataModel);
        OperandTable.setFillsViewportHeight(true);

        TableUtils.setAllMinColumnSize(OperandTable, columnWidths);
        TableUtils.setAllPreferredColumnSize(OperandTable, columnWidths);

        // Execute one step button action
        ExecuteOneStepBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slice[0]++;
                slice[1]++;
                update(dataModel);
            }
        });

        // Execute multiple step button action
        ExecuteMultipleStepBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slice[0]+=3;
                slice[1]+=3;
                update(dataModel);
            }
        });

        JFrame frame = new JFrame("Operands");
        frame.setContentPane(PanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 180);
        UICommonUtils.makeFrameToCenter(frame);
        frame.setVisible(true);
    }
}
