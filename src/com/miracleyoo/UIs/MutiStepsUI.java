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
import com.miracleyoo.UIs.DataUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class MutiStepsUI {
    private JButton ExitBtn;             // The exit button on the top-right.
    private JFormattedTextField InputTextField;   // The text area which get the user input.
    private BackgroundPanel MainPanel;   // The main panel.
    private Map<String, List<Object[]>> listFlagMap;   // The result of parse file.
    private static JFrame MainFrame = new NoneFrame();  // The main frame.
    private static int[] frameSize = new int[]{560, 315}; // The main frame size.

    MutiStepsUI() throws IOException {
        // Initialize the MainPanel
        BufferedImage img = null;
        img = ImageIO.read(new File("Assets/Input_multi_steps.png"));
        MainPanel = new BackgroundPanel(img, BackgroundPanel.SCALED, 1.0f, 0.5f);
        GradientPaint paint = new GradientPaint(0, 0, Color.BLUE, 600, 0, Color.RED);
        MainPanel.setPaint(paint);

        // Initialize the ExitBtn
        ExitBtn = new JButton();
        ExitBtn.setText("<html><font color='white'>×</font></html>");
        ExitBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        ExitBtn.setOpaque(false);
        ExitBtn.setContentAreaFilled(false);
        ExitBtn.setBorderPainted(false);

        // Initialize the InputTextField
        NumberFormat longFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(longFormat);
        numberFormatter.setValueClass(Long.class); //optional, ensures you will always get a long value
        numberFormatter.setAllowsInvalid(false); //this is the key!!
        numberFormatter.setMinimum(0L); //Optional
        InputTextField = new JFormattedTextField(numberFormatter);
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

        // Set focus on InputTextField when window show
        MainFrame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                InputTextField.requestFocus();
            }
        });

        // Action on ExitBtn: Exit the program
        ExitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.dispose();
            }
        });

        // Initialize the MainFrame and set bounds
        MainFrame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(MainFrame);
        MainFrame.setContentPane(MainPanel);
        MainFrame.getContentPane().setLayout(null);

        // Set bounds of the components
        ExitBtn.setBounds(MainFrame.getWidth() - 30, 0, 30, 22);
        InputTextField.setBounds(MainFrame.getWidth() / 2 + 10, MainFrame.getHeight() / 2 - 4,
                MainFrame.getWidth() / 4 - 20, 30);

        // Add components to the MainFrame
        MainPanel.add(ExitBtn);
        MainPanel.add(InputTextField);
        MainFrame.setContentPane(MainPanel);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
        MainFrame.setResizable(false);
        UICommonUtils.makeFrameToCenter(MainFrame);
    }
}
