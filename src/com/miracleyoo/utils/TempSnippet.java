package com.miracleyoo.utils;


import javax.swing.*;
import java.util.Arrays;

public class TempSnippet {
//    static private Object[][] fullData = {
//            {"Kathy", "Smith",
//                    "Snowboarding", 5, Boolean.FALSE},
//            {"John", "Doe",
//                    "Rowing", 3, Boolean.TRUE},
//            {"Sue", "Black",
//                    "Knitting", 2, Boolean.FALSE},
//            {"Jane", "White",
//                    "Speed reading", 20, Boolean.TRUE},
//            {"Joe", "Brown",
//                    "Pool", 10, Boolean.FALSE},
//            {"Kathy1", "Smith",
//                    "Snowboarding", 5, Boolean.FALSE},
//            {"John1", "Doe",
//                    "Rowing", 3, Boolean.TRUE},
//            {"Sue1", "Black",
//                    "Knitting", 2, Boolean.FALSE},
//            {"Jane1", "White",
//                    "Speed reading", 20, Boolean.TRUE},
//            {"Joe1", "Brown",
//                    "Pool", 10, Boolean.FALSE}
//    };

    // Data which will be shown on the table
//    private static Object[][] rawData = Arrays.copyOfRange(fullData, 0, 5);
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("App");
//        frame.setContentPane(new DataUI(rawData, columnNames).PanelMain);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }

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
// Pink, carnation, light blue, light green, light purple
    // Black, Blue, light blue(#AFDCF1), light grey, blue grey
//    public static final String[] colorSchemeMainDark =new String[]{"#2B2B2B","#5BB7E3", "#E4FDFE", "#DCDCDC", "#3F5467"};
    // Sakura, light blue, light green, millet yellow, grey
//    public static final String[] colorSchemeMainLight =new String[]{"#FEDFE1","#DFFEFC","#DFFEED","#FEFCDF", "#565656"};


    // Update the operand model when data are updated
    /*
    private void operandTableUpdate() {
        if (operandSlice[0] >= operandFullData.length - 1) {
            operandRawData = null;
        } else {
            operandRawData = Arrays.copyOfRange(operandFullData, operandSlice[0], operandSlice[1]);
        }
        operandModel.setDataVector(operandRawData, operandColumnNames);
        operandModel.fireTableDataChanged();
        TableUtils.setAllMinColumnSize(OperandTable, operandColumnWidths);
        TableUtils.setAllPreferredColumnSize(OperandTable, operandColumnWidths);
    }

     */

/*
    // Initialize the operand Table
    private void initOperandTable(Object[][] inputTotalData) {
        operandFullData = inputTotalData;
        operandRawData = Arrays.copyOfRange(operandFullData, operandSlice[0], operandSlice[1]);
        operandColumnNames = new String[]{"PC", "Operand"};
        operandModel = new DefaultTableModel(operandRawData, operandColumnNames);

        OperandTable.setModel(operandModel);
        OperandTable.setFillsViewportHeight(true);

        TableUtils.setAllMinColumnSize(OperandTable, operandColumnWidths);
        TableUtils.setAllPreferredColumnSize(OperandTable, operandColumnWidths);
    }

 */
    /*
    //When Reservation Stations are updated, need to refresh the Tomasulo Graph
    public void updateGraphPanel(){
        GraphPanel.validate();
        GraphPanel.repaint();
    }
     */

    //        if ((OperationInfoStation.get(i).SourceReg1==null || getReg(i, "Src1").ready ||
//                getReg(i, "Src1").occupyInstId == OperationInfoStation.get(i).absoluteIndex) &&
//                (OperationInfoStation.get(i).SourceReg2==null || getReg(i, "Src2").ready ||
//                        getReg(i, "Src2").occupyInstId == OperationInfoStation.get(i).absoluteIndex)){
//            if(OperationInfoStation.get(i).SourceReg1!=null) {
//                getReg(i, "Src1").ready = false;
//                getReg(i, "Src1").occupyInstId=OperationInfoStation.get(i).absoluteIndex;
//            }
//            if(OperationInfoStation.get(i).SourceReg2!=null) {
//                getReg(i, "Src2").ready = false;
//                getReg(i, "Src2").occupyInstId=OperationInfoStation.get(i).absoluteIndex;
//            }
//            if(OperationInfoStation.get(i).DestReg!=null) {
//                getReg(i, "Dest").ready = false;
//                getReg(i, "Dest").occupyInstId=OperationInfoStation.get(i).absoluteIndex;
//            }
//            return true;
//        }

//    private final int[] operandSlice = {0, 5};


}
