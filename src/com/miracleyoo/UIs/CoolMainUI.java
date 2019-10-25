/**
 * Functionality: A cool welcome interface. It will ask user to select
 *   a *.s file and parse the file into a list of map. Each line will be
 *   put into the corresponding array as a item.
 * Feature: It initialize a window which is boarder-less and mouse-drag-able.
 *   Also, a automatically scalable background image is added.
 * */

package com.miracleyoo.UIs;

import com.miracleyoo.utils.BackgroundPanel;
import com.miracleyoo.utils.NoneFrame;
import com.miracleyoo.utils.ParseFile;
import com.miracleyoo.utils.UICommonUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CoolMainUI {
    private JButton ChooseFileBtn;     // The button used to choose a *.s file.
    private JButton ExitBtn;           // The exit button on the top-right.
    private BackgroundPanel MainPanel; // The main panel.
    private JLabel FileSelectedCap;    // Welcome label.
    private String selectedFileName;   // The name of the user selected file.
    private Map< String, List<Object[]>> listFlagMap;   // The result of parse file.
    private static JFrame MainFrame = new NoneFrame();      // The main frame.
    private static int[] frameSize= new int[]{500,250}; // The main frame size.

    private CoolMainUI() throws IOException {
        // Initialize the MainPanel
        BufferedImage img = null;
        img = ImageIO.read(new File("Assets/image_01.jpg"));
        MainPanel = new BackgroundPanel(img, BackgroundPanel.SCALED, 1.0f, 0.5f);
        GradientPaint paint = new GradientPaint(0, 0, Color.BLUE, 600, 0, Color.RED);
        MainPanel.setPaint(paint);

        // Initialize the FileSelectedCap
        FileSelectedCap = new JLabel("<html><font color='white'>Welcome! Please select a *.s file to start!</font></html>");
        FileSelectedCap.setHorizontalAlignment(SwingConstants.CENTER);
        FileSelectedCap.setVerticalAlignment(SwingConstants.CENTER);
        FileSelectedCap.setFont(new Font("Dialog", Font.BOLD, 20));

        // Initialize the ChooseFileBtn
        ChooseFileBtn = new JButton();
        ChooseFileBtn.setText("<html><font color='white'>Select a *.s file to start</font></html>");
        ChooseFileBtn.setFont(new Font("Dialog", Font.BOLD, 15));
        ChooseFileBtn.setOpaque(false);
        ChooseFileBtn.setContentAreaFilled(false);
        ChooseFileBtn.setBorderPainted(false);

        // Initialize the ExitBtn
        ExitBtn = new JButton();
        ExitBtn.setText("<html><font color='white'>×</font></html>");
        ExitBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        ExitBtn.setOpaque(false);
        ExitBtn.setContentAreaFilled(false);
        ExitBtn.setBorderPainted(false);

        // Action on ChooseFileBtn
        ChooseFileBtn.addActionListener(new ActionListener() {
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
                    // Load and try to parse the file
                    selectedFileName = filePath + fileName;
                    listFlagMap = ParseFile.parseFile(new File(selectedFileName));
                    Object[][] operandListArray = listFlagMap.get("textList").toArray(new Object[0][0]);
                    Object[][] dataListArray = listFlagMap.get("dataList").toArray(new Object[0][0]);
                    new DataUI(operandListArray, dataListArray);
                    MainFrame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Action on ExitBtn: Exit the program
        ExitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        // Initialize the MainFrame and set bounds
        CoolMainUI coolMainUI = new CoolMainUI();
        MainFrame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(MainFrame);
        MainFrame.setContentPane(coolMainUI.MainPanel);
        MainFrame.getContentPane().setLayout(null);

        // Set bounds of the components
        coolMainUI.FileSelectedCap.setBounds(0, 10, MainFrame.getWidth() - 20, 30);
        coolMainUI.ExitBtn.setBounds(MainFrame.getWidth() - 30, 0, 30, 22);
        coolMainUI.ChooseFileBtn.setBounds(0, MainFrame.getHeight() - 60, MainFrame.getWidth(), 60);

        // Add components to the MainFrame
//        MainFrame.getContentPane().add(coolMainUI.FileSelectedCap);
        MainFrame.getContentPane().add(coolMainUI.ChooseFileBtn);
        MainFrame.getContentPane().add(coolMainUI.ExitBtn);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
        MainFrame.setResizable(false);
    }
}
