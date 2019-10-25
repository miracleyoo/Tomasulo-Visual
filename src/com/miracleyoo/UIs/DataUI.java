package com.miracleyoo.UIs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.miracleyoo.utils.*;

public class DataUI {
    private JPanel PanelMain;               // Main Panel
    private JButton ExecuteOneStepBtn;      // Execute One Step Button
    private JButton ExecuteMultipleStepBtn; // Execute Multiple Steps Button
    private JTable OperandTable;            // Table of operands
    private JTable RegisterTable;           // Table of registers
    private JTable DataTable;               // Table of data
    private JLabel StatisticsLabel;         // Label to show statistics data

    // Define the data, models, infos of all panels
    static private Object[][] operandFullData, operandRawData, dataFullData;
    static private Object[][] registerData = new Object[32][4];
    static private String[] operandColumnNames, registerColumnNames, dataColumnNames;
    static private DefaultTableModel operandModel, registerModel, dataModel;
    static private int[] statisticsInfo = new int[9];
    static private int multiStepNum = 3;

    // Define some const
    private static final int[] operandColumnWidths = new int[]{50, 500};
    private static final int[] registerColumnWidths = new int[]{20, 80, 20, 80};
    private static final int[] dataColumnWidths = new int[]{50, 500};
    private static final int[] frameSize = new int[]{800, 600};
    private static final int[] operandSlice = {0, 5};

    // Update the operand model when data are updated
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

    // Update the data model when data are updated
    private void dataTableUpdate(){
        dataModel.setDataVector(dataFullData, dataColumnNames);
        dataModel.fireTableDataChanged();
        TableUtils.setAllMinColumnSize(DataTable, dataColumnWidths);
        TableUtils.setAllPreferredColumnSize(DataTable, dataColumnWidths);
    }

    // Reset all panels and tables
    private void ResetALLData(){
        operandSlice[0] = 0;
        operandSlice[1]= 5;
        operandTableUpdate();
        dataTableUpdate();
        initRegisterTable();
        initStatisticsPanel();
    }

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

    // Initialize the register Table
    private void initRegisterTable() {
        registerColumnNames = new String[]{"IntReg", "Value", "FloatReg", "Value"};
        for (int i = 0; i < 32; i++) {
            registerData[i][0] = "R" + i + "=";
            registerData[i][1] = String.format("%08d", 0);
            registerData[i][2] = "F" + i + "=";
            registerData[i][3] = String.format("%.8f", 0.0);
        }
        registerModel = new DefaultTableModel(registerData, registerColumnNames);

        RegisterTable.setModel(registerModel);
        RegisterTable.setFillsViewportHeight(true);

        TableUtils.setAllMinColumnSize(RegisterTable, registerColumnWidths);
        TableUtils.setAllPreferredColumnSize(RegisterTable, registerColumnWidths);
    }

    // Initialize the Data Table
    private void initDataTable(Object[][] inputData) {
        dataFullData = inputData;
        dataColumnNames = new String[]{"PC", "Content"};
        dataModel = new DefaultTableModel(dataFullData, dataColumnNames);

        DataTable.setModel(dataModel);
        DataTable.setFillsViewportHeight(true);

        TableUtils.setAllMinColumnSize(DataTable, dataColumnWidths);
        TableUtils.setAllPreferredColumnSize(DataTable, dataColumnWidths);
    }

    // Initialize Statistics Panel
    private void initStatisticsPanel() {
        StatisticsLabel.setText(
                "<html><font color='red'>Execution</font><br>" +
                        statisticsInfo[0] + " Cycles<br>" +
                        statisticsInfo[1] + " Instructions<br><br>" +
                        "<font color='red'>Stalls<br></font>" +
                        statisticsInfo[2] + " RAW Stalls<br>" +
                        statisticsInfo[3] + " WAW Stalls<br>" +
                        statisticsInfo[4] + " WAR Stalls<br>" +
                        statisticsInfo[5] + " Structural Stalls<br>" +
                        statisticsInfo[6] + " Branch Taken Stalls<br>" +
                        statisticsInfo[7] + " Branch Mis-prediction Stalls<br><br>" +
                        "<font color='red'>Code Size</font><br>" +
                        statisticsInfo[8] + " Bytes"
        );
    }

    // Define the menu bar
    private JMenuBar addMenuBar() {
        // Start dealing with MenuBar
        JMenuBar mainMenuBar = new JMenuBar();

        // Menus
        JMenu mFile = new JMenu("File");
        JMenu mExec = new JMenu("Execute");
        JMenu mConf = new JMenu("Configure");
        JMenu mWind = new JMenu("Window");
        JMenu mHelp = new JMenu("Help");

        // Add menus to menu bar
        mainMenuBar.add(mFile);
        mainMenuBar.add(mExec);
        mainMenuBar.add(mConf);
        mainMenuBar.add(mWind);
        mainMenuBar.add(mHelp);

        // Add menu items to menu mFile
        String[] fileItemNames = {"Open", "Reset", "Full Reset", "Exit"};
        Map<String, JMenuItem> fileItems = new HashMap<String, JMenuItem>();
        for (String itemName : fileItemNames) {
            fileItems.put(itemName, new JMenuItem(itemName));
            mFile.add(fileItems.get(itemName));
        }

        // Add listener to Open, here a *.s file is needed, if the file chooser successfully get
        // a *.s file, it will open DataUI and pass the opened file to it.
        fileItems.get("Open").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
                fd.setDirectory("~/Downloads/");
                fd.setFilenameFilter((dir, name) -> name.endsWith(".s"));
                fd.setVisible(true);
                String fileName = fd.getFile();
                String filePath = fd.getDirectory();
                if (fileName == null)
                    System.out.println("You cancelled the choice");
                else
                    System.out.println("You chose " + filePath + fileName);
                try {
                    String selectedFileName = filePath + fileName;
                    Map<String, List<Object[]>> listFlagMap = ParseFile.parseFile(new File(selectedFileName));
                    operandFullData = listFlagMap.get("textList").toArray(new Object[0][0]);
                    dataFullData = listFlagMap.get("dataList").toArray(new Object[0][0]);
                    ResetALLData();
                    // TODO: Add a global init function
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        fileItems.get("Reset").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResetALLData();
            }
        });

        fileItems.get("Exit").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Add menu items to menu mExec
        String[] execItemNames = {"Single Cycle", "Multi Cycles", "Run to", "Stop"};
        Map<String, JMenuItem> execItems = new HashMap<String, JMenuItem>();
        for (String itemName : execItemNames) {
            execItems.put(itemName, new JMenuItem(itemName));
            mExec.add(execItems.get(itemName));
        }

        execItems.get("Single Cycle").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExeSteps(1);
            }
        });

        execItems.get("Multi Cycles").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExeSteps(multiStepNum);
            }
        });

        // Add menu items to menu mConf
        // Here may need some other Configures
        String[] confItemNames = {"Architecture", "Multi-Step"};
        Map<String, JMenuItem> confItems = new HashMap<String, JMenuItem>();
        for (String itemName : confItemNames) {
            confItems.put(itemName, new JMenuItem(itemName));
            mConf.add(confItems.get(itemName));
        }

        // Add menu items to menu mWind
        String[] windItemNames = {"Code", "Statistics", "Data", "Registers", "Pipeline", "Cycles", "Terminal"};
        Map<String, JMenuItem> windItems = new HashMap<String, JMenuItem>();
        for (String itemName : windItemNames) {
            windItems.put(itemName, new JMenuItem(itemName));
            mWind.add(windItems.get(itemName));
        }

        // Add menu items to menu mHelp
        String[] helpItemNames = {"About Tomasulo Visual"};
        Map<String, JMenuItem> helpItems = new HashMap<String, JMenuItem>();
        for (String itemName : helpItemNames) {
            helpItems.put(itemName, new JMenuItem(itemName));
            mHelp.add(helpItems.get(itemName));
        }

        helpItems.get("About Tomasulo Visual").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new InfoUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return mainMenuBar;
    }

    private void ExeSteps(int stepNum){
        operandSlice[0]+= stepNum;
        operandSlice[1]+= stepNum;
        operandTableUpdate();
    }

    public DataUI(Object[][] inputOperandFieldData, Object[][] inputDataFieldData) {
        // Initialize the operand Table
        initOperandTable(inputOperandFieldData);

        // Initialize the register Table
        initRegisterTable();

        // Initialize the data Table
        initDataTable(inputDataFieldData);

        // Initialize the Statistics Panel
        initStatisticsPanel();

        // Execute one step button action
        ExecuteOneStepBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExeSteps(1);
            }
        });

        // Execute multiple step button action
        ExecuteMultipleStepBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExeSteps(multiStepNum);
            }
        });

        // Initiate the DataUI window
        JFrame frame = new JFrame("Operands");
        frame.setContentPane(PanelMain);
        frame.setJMenuBar(addMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(frame);
        frame.setVisible(true);
    }
}
