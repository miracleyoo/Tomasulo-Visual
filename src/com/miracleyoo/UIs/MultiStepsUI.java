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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

class MultiStepsUI {
    private JFormattedTextField InputTextField;   // The text area which get the user input.
    private static JFrame MainFrame = new NoneFrame();  // The main frame.
    private static int[] frameSize = new int[]{560, 315}; // The main frame size.

    MultiStepsUI() throws IOException {
        // Initialize the MainPanel
        BufferedImage img;
        img = ImageIO.read(new File("Assets/Input_multi_steps.png"));
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

        // Initialize the InputTextField
        NumberFormat longFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(longFormat);
        numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
        numberFormatter.setAllowsInvalid(false); //this is the key!!
        numberFormatter.setMinimum(0L); //Optional
        InputTextField = new JFormattedTextField(numberFormatter);
        InputTextField.setValue(DataUI.multiStepNum);
        InputTextField.setOpaque(false);

        // Add action to EnterTextAction. Execute when "enter" is pressed.
        // This action will set the multiStepNum in DataUI to the Value set.
        Action EnterTextAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataUI.multiStepNum = (long) InputTextField.getValue();
                MainFrame.dispose();
            }
        };
        InputTextField.addActionListener(EnterTextAction);

        // Make the InputTextField automatically select all text when it get focus.
        InputTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        InputTextField.selectAll();
                    }
                });
            }
        });

        // Set focus on InputTextField when window show
        MainFrame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                InputTextField.requestFocus();
            }
        });

        // Action on ExitBtn: Exit the program
        exitBtn.addActionListener(e -> MainFrame.dispose());

        // Initialize the MainFrame and set bounds
        MainFrame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(MainFrame);
        MainFrame.setContentPane(mainPanel);
        MainFrame.getContentPane().setLayout(null);

        // Set bounds of the components
        exitBtn.setBounds(MainFrame.getWidth() - 30, 0, 30, 22);
        InputTextField.setBounds(MainFrame.getWidth() / 2 + 10, MainFrame.getHeight() / 2 - 4,
                MainFrame.getWidth() / 4 - 20, 30);

        // Add components to the MainFrame
        mainPanel.add(exitBtn);
        mainPanel.add(InputTextField);
        MainFrame.setContentPane(mainPanel);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
        MainFrame.setResizable(false);
        UICommonUtils.makeFrameToCenter(MainFrame);
    }
}
