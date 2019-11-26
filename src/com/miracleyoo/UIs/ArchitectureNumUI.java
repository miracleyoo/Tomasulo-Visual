/**
 * Functionality: A cool welcome interface. It will ask user to select
 * a *.s file and parse the file into a list of map. Each line will be
 * put into the corresponding array as a item.
 * Feature: It initialize a window which is boarder-less and mouse-drag-able.
 * Also, a automatically scalable background image is added.
 */

package com.miracleyoo.UIs;

import com.miracleyoo.utils.BackgroundPanel;
import com.miracleyoo.utils.NoneFrame;
import com.miracleyoo.utils.UICommonUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

class ArchitectureNumUI {
    private List<JFormattedTextField> InputTextField = new ArrayList<>();   // The text area which get the user input.
    private JFrame MainFrame = new NoneFrame();  // The main frame.
    private int[] frameSize = new int[]{560, 315}; // The main frame size.
    //static DataUI DataUIFrame = new DataUI(new Object[0][0], new Object[0][0]);

    private void summarizeAction(){
        for(int i = 0; i<DataUI.architectureNum.length; i++) {

            if((long) InputTextField.get(i).getValue() > 9) { //max
                DataUI.architectureNum[i] = 9; //Max allowable value for RS
            }

            else if((long) InputTextField.get(i).getValue() < 1){
                DataUI.architectureNum[i] = 1; //min allowable value for RS
            }

            else{
                DataUI.architectureNum[i] = (long) InputTextField.get(i).getValue(); //---ADD LIMITATIONS FOR RS'S---
            }
        }
        CoolMainUI.DataUIFrame.ResetALLData();
        //need to update diagram if reservation stations have been changed.
        //DataUIFrame.updateGraphPanel();
        System.out.println("Architecture updated");
        MainFrame.dispose();
    }

    ArchitectureNumUI() throws IOException {
        // Initialize the MainPanel
        BufferedImage img;
        img = ImageIO.read(new File("Assets/ArchitectureNum.png"));
        // The main panel.
        BackgroundPanel mainPanel = new BackgroundPanel(img, BackgroundPanel.SCALED, 1.0f, 0.5f);
        GradientPaint paint = new GradientPaint(0, 0, Color.BLUE, 600, 0, Color.RED);
        mainPanel.setPaint(paint);

        // Initialize the ExitBtn
        // The exit button on the top-right.
        JButton exitBtn = new JButton();
        exitBtn.setText("<html><font color='white'>×</font></html>");
        exitBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        exitBtn.setOpaque(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setBorderPainted(false);

        // Initialize the submitBtn
        JButton submitBtn = new JButton();
        submitBtn.setText("");
        submitBtn.setOpaque(false);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setBorderPainted(false);

        // Initialize the InputTextField
        NumberFormat longFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(longFormat);
        numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
        numberFormatter.setAllowsInvalid(false); //this is the key!!
        numberFormatter.setMinimum(0L); //Optional

        // Action on ExitBtn: Exit the program
        exitBtn.addActionListener(e -> MainFrame.dispose());

        // Action on submitBtn
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                summarizeAction();
        }
        });

        // Initialize the MainFrame and set bounds
        MainFrame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(MainFrame);
        MainFrame.setContentPane(mainPanel);
        MainFrame.getContentPane().setLayout(null);

        // Set bounds of the components
        exitBtn.setBounds(MainFrame.getWidth() - 30, 0, 30, 22);
        submitBtn.setBounds(MainFrame.getWidth()/2 + 80, MainFrame.getHeight()*5/6+3, 95, 30);

        // Initialize all InputTextFields and bound listeners to them.
        final int[] outerCounter = {0};
        for(outerCounter[0]=0; outerCounter[0]<DataUI.architectureNum.length; outerCounter[0]++) {
            int innerCounter = outerCounter[0];
            InputTextField.add(new JFormattedTextField(numberFormatter));
            // Set the default value of each InputTextField by corresponding architecture values.
            InputTextField.get(innerCounter).setValue(DataUI.architectureNum[innerCounter]);
            InputTextField.get(innerCounter).setOpaque(false);

            // Make each InputTextField automatically select all text when it get focus.
            InputTextField.get(innerCounter).addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            InputTextField.get(innerCounter).selectAll();
                        }
                    });
                }
            });

            // Add action to EnterTextAction. Execute when "enter" is pressed.
            // This action will set the multiStepNum in DataUI to the Value set.
            if(innerCounter<=3) {
                InputTextField.get(innerCounter).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DataUI.architectureNum[innerCounter] = (long) InputTextField.get(innerCounter).getValue();
                        // Make the next InputTextField selected when press enter
                        InputTextField.get(innerCounter+1).requestFocus();
                    }
                });
            }
            else{
                InputTextField.get(innerCounter).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DataUI.architectureNum[innerCounter] = (long) InputTextField.get(innerCounter).getValue();
                        // Save all value set and close this window when user press enter at the last InputTextField
                        summarizeAction();
                    }
                });
            }

            // Set bound of the InputTextFields
            InputTextField.get(innerCounter).setBounds(MainFrame.getWidth() / 2 + 10, MainFrame.getHeight() / 3 + innerCounter * 30 - 34,
                    MainFrame.getWidth() / 4 - 20, 30);

            // Add InputTextFields to the mainPanel
            mainPanel.add(InputTextField.get(innerCounter));
        }

        // Set focus on InputTextField when window show
        MainFrame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                InputTextField.get(0).requestFocus();
            }
        });

        // Add components to the MainFrame
        mainPanel.add(exitBtn);
        mainPanel.add(submitBtn);

        MainFrame.setContentPane(mainPanel);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
        MainFrame.setResizable(false);
        UICommonUtils.makeFrameToCenter(MainFrame);
    }
}
