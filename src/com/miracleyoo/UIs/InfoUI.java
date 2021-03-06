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
import com.miracleyoo.utils.UICommonUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class InfoUI {
    private static JFrame MainFrame = new NoneFrame();      // The main frame.
    private static int[] frameSize= new int[]{560,315}; // The main frame size.

    InfoUI() throws IOException {
        // Initialize the MainPanel
        BufferedImage img;
        img = ImageIO.read(new File("Assets/info.png"));
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

        // Action on ExitBtn: Exit the program
        exitBtn.addActionListener(e -> MainFrame.dispose());

        // Initialize the MainFrame and set bounds
        MainFrame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(MainFrame);
        MainFrame.setContentPane(mainPanel);
        MainFrame.getContentPane().setLayout(null);

        // Set bounds of the components
        exitBtn.setBounds(MainFrame.getWidth() - 30, 0, 30, 25);

        // Add components to the MainFrame
        mainPanel.add(exitBtn);
        MainFrame.setContentPane(mainPanel);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.setVisible(true);
        MainFrame.setResizable(false);
        UICommonUtils.makeFrameToCenter(MainFrame);
    }
}
