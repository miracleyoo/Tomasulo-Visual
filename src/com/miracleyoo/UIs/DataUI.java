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

import static java.lang.Integer.min;

public class DataUI {
    private JPanel PanelMain;               // Main Panel
    private JButton ExecuteOneStepBtn;      // Execute One Step Button
    private JButton ExecuteMultipleStepBtn; // Execute Multiple Steps Button
    private JTable OperandTable;            // Table of operands
    private JTable RegisterTable;           // Table of registers
    private JTable DataTable;               // Table of data
    private JLabel StatisticsText;         // Label to show statistics data
    private JTable CycleTable;
    private JLabel CycleLabel;
    private JLabel CodeLabel;
    private JLabel DataLabel;
    private JLabel RegisterLabel;
    private JLabel StatisticsLabel;
    private JLabel TomasuloLabel;
    private JScrollPane GraphPanel;
    private JScrollPane CyclePanel;

    // Define the data, models, infos of all panels
    static private Object[][] operandFullData, operandRawData, dataFullData, cycleFullData;
    static private Object[][] registerData = new Object[32][4];
    static private String[] operandColumnNames, registerColumnNames, dataColumnNames, cycleColumnNames;
    static private String[] cycleStageNames= new String[]{"IF", "ID", "EX", "MEM", "WB"};
    static private DefaultTableModel operandModel, registerModel, dataModel, cycleModel;
    static private int[] statisticsInfo = new int[9];
    static long architectureNum[] = new long[]{6, 6, 5, 4, 4, 3};
    static long architectureCycle[] = new long[]{10, 10, 4, 7, 24, 5};
    static long multiStepNum = 3;

    // Whether using Dark Mode or not(Light Mode)
    public static boolean DarkMode=false;

    public static final String[] colorSchemeCycleLight =new String[]{"#F8C3CD","#FFE2C9","#A9D5D7","#CEF1BE","#BBB6E0"};
    public static final String[] colorSchemeCycleDark =new String[]{"#FFAC5E","#4ACFAC","#7E8CE0","#3DC7D0","#FFA48E"};

    // Table Background || Table Foreground || Table Header Background || Table Header Foreground || Label Color || Main Background || Main Foreground || High Light
    public static final String[] colorSchemeMainLight = new String[]{"#DFFEFC", "#565656", "#FEFCDF", "#565656", "#565656", "#FEDFE1", "#2B2B2B", "#5BB7E3"};
    public static final String[] colorSchemeMainDark = new String[]{"#2B2B2B" ,"#DCDCDC" ,"#3F5467", "#E4FDFE" ,"#E4FDFE", "#2B2B2B", "#FFFFFF", "#5BB7E3"};



    public static String[] colorSchemeMainCur = DarkMode? colorSchemeMainDark:colorSchemeMainLight;
    public static String[] colorSchemeCycleCur = DarkMode? colorSchemeCycleDark:colorSchemeCycleLight;

    private int[] operandColumnWidths = new int[]{100, 400};
    private int[] registerColumnWidths = new int[]{120, 160, 120, 160};

    private int[] cycleColumnWidths;
    private int cycleColumnWidth = 100;
    private int cycleNum=0;


    private TableUtils.StatusColumnCellRenderer cycleTableRender = new TableUtils.StatusColumnCellRenderer();

    private static final int[] dataColumnWidths = new int[]{100, 400};
    private static final int[] frameSize = new int[]{1280, 720};
    private static final int[] operandSlice = {0, 5};

    // Initiate the DataUI window
    JFrame frame = new JFrame("Operands");


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
    private void dataTableUpdate() {
        dataModel.setDataVector(dataFullData, dataColumnNames);
        dataModel.fireTableDataChanged();
        TableUtils.setAllMinColumnSize(DataTable, dataColumnWidths);
        TableUtils.setAllPreferredColumnSize(DataTable, dataColumnWidths);
    }

    // Update the data model when data are updated
    private void cycleTableUpdate() {
        cycleColumnNames = new String[cycleNum];
        cycleFullData = new String[5][cycleNum];
        cycleColumnWidths = new int[cycleNum];
        Arrays.fill(cycleColumnWidths, 100);
        for (int i = 0; i < cycleNum; i++) {
            cycleColumnNames[i] = Integer.toString(i);
            for(int j=0; j<=min(i, 4); j++) {
                cycleFullData[j][i] = cycleStageNames[i<=4?i-j:4-j];
            }
        }
        cycleModel.setDataVector(cycleFullData, cycleColumnNames);
        cycleModel.fireTableDataChanged();
//        CycleTable.setFillsViewportHeight(true);

        for (int i = 0; i < cycleNum; i++) {
            CycleTable.getColumn(cycleColumnNames[i]).setCellRenderer(cycleTableRender);//new TableUtils.StatusColumnCellRenderer());
        }

        cycleColumnWidths = new int[cycleColumnNames.length];
        Arrays.fill(cycleColumnWidths, cycleColumnWidth);

        TableUtils.setAllMinColumnSize(CycleTable, cycleColumnWidths);
        TableUtils.setAllPreferredColumnSize(CycleTable, cycleColumnWidths);
    }

    // Reset all panels and tables
    void ResetALLData() {
        operandSlice[0] = 0;
        operandSlice[1] = 5;
        cycleNum = 0;
        CycleLabel.setText("Cycles(Preview)");
        operandTableUpdate();
        dataTableUpdate();
        initRegisterTable();
        initStatisticsPanel();
        initCycleTable();
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

    // Initialize the cycle Table
    private void initCycleTable() {
        cycleColumnNames = new String[]{"1", "2", "3", "4", "5"};
        cycleColumnWidths = new int[cycleNum];
        Arrays.fill(cycleColumnWidths, 100);

        cycleFullData = new String[5][5];
        for (int i = 0; i < 5; i++) {
            for(int j=0; j<=i; j++) {
                cycleFullData[j][i] = cycleStageNames[i-j];
            }
        }
        cycleModel = new DefaultTableModel(cycleFullData, cycleColumnNames);

        CycleTable.setModel(cycleModel);
        CycleTable.setFillsViewportHeight(true);

        for (int i = 0; i < 5; i++) {
            CycleTable.getColumn(cycleColumnNames[i]).setCellRenderer(cycleTableRender);
        }

        cycleColumnWidths = new int[cycleColumnNames.length];
        Arrays.fill(cycleColumnWidths, 100);

        TableUtils.setAllMinColumnSize(CycleTable, cycleColumnWidths);
        TableUtils.setAllPreferredColumnSize(CycleTable, cycleColumnWidths);
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

        // sets the background color of this component
        // the background color is used only if the component is opaque
        StatisticsText.setText(
                "<html><font color="+ colorSchemeMainCur[7]+"><b>Execution</b></font><br>" +
                        statisticsInfo[0] + " Cycles<br>" +
                        statisticsInfo[1] + " Instructions<br><br>" +
                        "<font color="+ colorSchemeMainCur[7]+"><b>Stalls</b></font><br>" +
                        statisticsInfo[2] + " RAW Stalls<br>" +
                        statisticsInfo[3] + " WAW Stalls<br>" +
                        statisticsInfo[4] + " WAR Stalls<br>" +
                        statisticsInfo[5] + " Structural Stalls<br>" +
                        statisticsInfo[6] + " Branch Taken Stalls<br>" +
                        statisticsInfo[7] + " Branch Mis-prediction Stalls<br><br>" +
                        "<font color="+ colorSchemeMainCur[7]+"><b>Code Size</b></font><br>" +
                        statisticsInfo[8] + " Bytes"
        );
    }

    // Tomasulo Diagram
    private void initGraphPanel() {
        JPanel d = new Diagram();
//        d.setBackground(Color.WHITE);
        d.setSize(new Dimension(Diagram.diagramWidth*10, Diagram.diagramHeight));

        GraphPanel.setViewportView(d);
        GraphPanel.revalidate();
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
        String[] fileItemNames = {"Open", "Reset", "Exit"};
        Map<String, JMenuItem> fileItems = new HashMap<>();
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

        // Reset current state
        fileItems.get("Reset").addActionListener(e -> ResetALLData());

        // Exit the program
        fileItems.get("Exit").addActionListener(e -> System.exit(0));

        // Add menu items to menu mExec
        String[] execItemNames = {"Single Cycle", "Multi Cycles", "Run to"};
        Map<String, JMenuItem> execItems = new HashMap<>();
        for (String itemName : execItemNames) {
            execItems.put(itemName, new JMenuItem(itemName));
            mExec.add(execItems.get(itemName));
        }

        // Execute one step
        execItems.get("Single Cycle").addActionListener(e -> ExeSteps(1));

        // Execute multiple steps
        execItems.get("Multi Cycles").addActionListener(e -> ExeSteps(multiStepNum));

        // Add menu items to menu mConf
        // Here may need some other Configures
        String[] confItemNames = {"Architecture Number", "Architecture Cycle", "Multi-Step", "Change Scheme"};
        Map<String, JMenuItem> confItems = new HashMap<>();
        for (String itemName : confItemNames) {
            confItems.put(itemName, new JMenuItem(itemName));
            mConf.add(confItems.get(itemName));
        }

        confItems.get("Multi-Step").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new MultiStepsUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        confItems.get("Architecture Number").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new ArchitectureNumUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        confItems.get("Architecture Cycle").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new ArchitectureCycleUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        confItems.get("Change Scheme").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DarkMode = !DarkMode;
                SetUIScheme();
                frame.validate();
                frame.repaint();
                frame.setVisible(true);
            }
        });

        // Add menu items to menu mWind
        String[] windItemNames = {"Code", "Statistics", "Data", "Registers", "Pipeline", "Cycles", "Terminal"};
        Map<String, JMenuItem> windItems = new HashMap<>();
        for (String itemName : windItemNames) {
            windItems.put(itemName, new JMenuItem(itemName));
            mWind.add(windItems.get(itemName));
        }

        // Add menu items to menu mHelp
        String[] helpItemNames = {"About Tomasulo Visual"};
        Map<String, JMenuItem> helpItems = new HashMap<>();
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

    private void ExeSteps(long stepNum) {
        operandSlice[0] += stepNum;
        operandSlice[1] += stepNum;
        if(cycleNum==0){
            CycleLabel.setText("Cycles");
        }
        cycleNum += stepNum;
        operandTableUpdate();
        cycleTableUpdate();
    }

    private void SetTableScheme(JTable renderTable, String[] ColorScheme){
        renderTable.setBackground(Color.decode(ColorScheme[0]));
        renderTable.setForeground(Color.decode(ColorScheme[1]));
        renderTable.getTableHeader().setBackground(Color.decode(ColorScheme[2]));
        renderTable.getTableHeader().setForeground(Color.decode(ColorScheme[3]));
    }


    // Set the scheme according to the UI color mode
    private void SetUIScheme(){
        // StatisticsText Setting
        StatisticsText.setVerticalAlignment(JLabel.TOP);
        StatisticsText.setVerticalTextPosition(JLabel.TOP);
        StatisticsText.setOpaque(true);

        // Color Mode Setting
        if(DarkMode) {
            colorSchemeMainCur = colorSchemeMainDark;
            colorSchemeCycleCur = colorSchemeCycleDark;
        }
        else {
            colorSchemeMainCur = colorSchemeMainLight;
            colorSchemeCycleCur = colorSchemeCycleLight;
        }

        // Set Main background color
        PanelMain.setBackground(Color.decode(colorSchemeMainCur[5]));
        PanelMain.setForeground(Color.decode(colorSchemeMainCur[6]));

        // Set labels
        TomasuloLabel.setForeground(Color.decode(colorSchemeMainCur[4]));
        CycleLabel.setForeground(Color.decode(colorSchemeMainCur[4]));
        StatisticsLabel.setForeground(Color.decode(colorSchemeMainCur[4]));
        CodeLabel.setForeground(Color.decode(colorSchemeMainCur[4]));
        DataLabel.setForeground(Color.decode(colorSchemeMainCur[4]));
        RegisterLabel.setForeground(Color.decode(colorSchemeMainCur[4]));

        SetTableScheme(RegisterTable, colorSchemeMainCur);
        SetTableScheme(OperandTable, colorSchemeMainCur);
        SetTableScheme(DataTable, colorSchemeMainCur);

        CyclePanel.getViewport().setBackground(Color.decode(colorSchemeMainCur[0]));
        SetTableScheme(CycleTable, colorSchemeMainCur);

        GraphPanel.getViewport().getView().setBackground(Color.decode(colorSchemeMainCur[0]));

        StatisticsText.setBackground(Color.decode(colorSchemeMainCur[0]));
        StatisticsText.setForeground(Color.decode(colorSchemeMainCur[6]));
    }

    DataUI(Object[][] inputOperandFieldData, Object[][] inputDataFieldData) {
        // Initialize the operand Table
        initOperandTable(inputOperandFieldData);

        // Initialize the register Table
        initRegisterTable();

        // Initialize the data Table
        initDataTable(inputDataFieldData);

        // Initialize the Statistics Panel
        initStatisticsPanel();

        // Initialize the Cycle Table
        initCycleTable();

        // Initialize Tomasulo Graph
        initGraphPanel();

        // Execute one step button action
        ExecuteOneStepBtn.addActionListener(e -> ExeSteps(1));

        // Execute multiple step button action
        ExecuteMultipleStepBtn.addActionListener(e -> ExeSteps(multiStepNum));



        // Set the scheme according to the UI color mode
        SetUIScheme();

        frame.setContentPane(PanelMain);
        frame.setJMenuBar(addMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(frameSize[0], frameSize[1]);
        frame.setMinimumSize(new Dimension(frameSize[0], frameSize[1]));
        UICommonUtils.makeFrameToCenter(frame);
        frame.setVisible(true);
    }
}
