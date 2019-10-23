package com.miracleyoo.UIs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class DataUI {
//    private class OperandModel extends AbstractTableModel {
//        String[] columnNames = {"First Name",
//                "Last Name",
//                "Sport",
//                "# of Years",
//                "Vegetarian"};
//
//        Object[][] rowData = {
//                {"Kathy", "Smith",
//                        "Snowboarding", 5, Boolean.FALSE},
//                {"John", "Doe",
//                        "Rowing", 3, Boolean.TRUE},
//                {"Sue", "Black",
//                        "Knitting", 2, Boolean.FALSE},
//                {"Jane", "White",
//                        "Speed reading", 20, Boolean.TRUE},
//                {"Joe", "Brown",
//                        "Pool", 10, Boolean.FALSE}
//        };
//
//        @Override
//        public int getRowCount() {
//            return rowData.length;
//        }
//
//        @Override
//        public int getColumnCount() {
//            return rowData[0].length;
//        }
//
//        @Override
//        public String getColumnName(int column) {
//            return columnNames[column];
//        }
//
//        @Override
//        public Object getValueAt(int rowIndex, int columnIndex) {
//            return rowData[rowIndex][columnIndex];
//        }
//    }

    private JPanel PanelMain;
    private JButton ExecuteOneStepBtn;
    private JButton ExecuteMultipleStepBtn;
    private JTable OperandTable;
    private final int[] slice = {0,5};

    // Column names for the OperandTable
    private String[] columnNames = {"First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"};

    // Total data for the OperandTable
    private Object[][] fullData = {
            {"Kathy", "Smith",
                    "Snowboarding", 5, Boolean.FALSE},
            {"John", "Doe",
                    "Rowing", 3, Boolean.TRUE},
            {"Sue", "Black",
                    "Knitting", 2, Boolean.FALSE},
            {"Jane", "White",
                    "Speed reading", 20, Boolean.TRUE},
            {"Joe", "Brown",
                    "Pool", 10, Boolean.FALSE},
            {"Kathy1", "Smith",
                    "Snowboarding", 5, Boolean.FALSE},
            {"John1", "Doe",
                    "Rowing", 3, Boolean.TRUE},
            {"Sue1", "Black",
                    "Knitting", 2, Boolean.FALSE},
            {"Jane1", "White",
                    "Speed reading", 20, Boolean.TRUE},
            {"Joe1", "Brown",
                    "Pool", 10, Boolean.FALSE}
    };

    // Data which will be shown on the table
    private Object[][] rawData = Arrays.copyOfRange(fullData, 0, 5);

    private void update() {
        if(slice[0]>=fullData.length-1){
            rawData = new Object[][]{{"", "", "", "", ""}};
        }
        else {
            rawData = Arrays.copyOfRange(fullData, slice[0], slice[1]);
        }
        TableModel dataModel = new DefaultTableModel(rawData,columnNames);
        OperandTable.setModel(dataModel);
    }

    private DataUI() {
        // Configure the table
        TableModel dataModel = new DefaultTableModel(rawData,columnNames);
        OperandTable.setModel(dataModel);
        OperandTable.setPreferredScrollableViewportSize(new Dimension(1000, 70));
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
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new DataUI().PanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
