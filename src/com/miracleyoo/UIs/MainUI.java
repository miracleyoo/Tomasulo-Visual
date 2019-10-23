package com.miracleyoo.UIs;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.miracleyoo.utils.*;

public class MainUI {
    private JButton ChooseFileBtn;
    private JPanel PanelMain;
    private JLabel FileSelectedCap;
    private JLabel FileSelectedVal;
    private File selectedFile;

    private MainUI() {
        assert ChooseFileBtn != null;
        ChooseFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc=new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
                jfc.showDialog(new JLabel(), "Choose");
                File file=jfc.getSelectedFile();
                System.out.println("==> File:"+file.getAbsolutePath());
                System.out.println(jfc.getSelectedFile().getName());
                ParseFile parser = new ParseFile();
                try {
                    parser.parseFile(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

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
        Map< String, JMenuItem> fileItems = new HashMap< String, JMenuItem>();
        for (String itemName:fileItemNames){
            fileItems.put(itemName, new JMenuItem(itemName));
            mFile.add(fileItems.get(itemName));
        }

        // Add listener to Open
        fileItems.get("Open").addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc=new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
                jfc.showDialog(new JLabel(), "Choose");
                File file=jfc.getSelectedFile();
//                System.out.println("==> File:"+file.getAbsolutePath());
//                System.out.println(jfc.getSelectedFile().getName());
                FileSelectedVal.setText(file.getAbsolutePath());
                selectedFile = file;
            }
        });

        // Add menu items to menu mExec
        String[] execItemNames = {"Single Cycle", "Multi Cycles", "Run to", "Stop"};
        Map< String, JMenuItem> execItems = new HashMap< String, JMenuItem>();
        for (String itemName:execItemNames){
            execItems.put(itemName, new JMenuItem(itemName));
            mExec.add(execItems.get(itemName));
        }

        // Add menu items to menu mConf
        // Here may need some other Configures
        String[] confItemNames = {"Architecture", "Multi-Step"};
        Map< String, JMenuItem> confItems = new HashMap< String, JMenuItem>();
        for (String itemName:confItemNames){
            confItems.put(itemName, new JMenuItem(itemName));
            mConf.add(confItems.get(itemName));
        }

        // Add menu items to menu mWind
        String[] windItemNames = {"Code", "Statistics", "Data", "Registers", "Pipeline", "Cycles", "Terminal"};
        Map< String, JMenuItem> windItems = new HashMap< String, JMenuItem>();
        for (String itemName:windItemNames){
            windItems.put(itemName, new JMenuItem(itemName));
            mWind.add(windItems.get(itemName));
        }

        // Add menu items to menu mHelp
        String[] helpItemNames = {"About Tomasulo Visual"};
        Map< String, JMenuItem> helpItems = new HashMap< String, JMenuItem>();
        for (String itemName:helpItemNames){
            helpItems.put(itemName, new JMenuItem(itemName));
            mHelp.add(helpItems.get(itemName));
        }

        return mainMenuBar;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        MainUI mainUI = new MainUI();
        frame.setContentPane(mainUI.PanelMain);

        // Add menu bar to frame
        frame.setJMenuBar(mainUI.addMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
