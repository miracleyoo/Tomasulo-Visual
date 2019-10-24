package com.miracleyoo.utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;

public class NoneFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private Point pressedPoint;

    public NoneFrame() {
        this.getContentPane().setBackground(new Color(195, 184, 162)); // Set background color
        this.setUndecorated(true); // Invalidate window decoration
        this.getContentPane().setLayout(null); // Window use absolute layout
        this.setLocationRelativeTo(null); // Move window to the center
        this.setAlwaysOnTop(true); // Show window on the top

        /*
          Drag window by mouse
         */
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { // Mouse click
                pressedPoint = e.getPoint(); // Record mouse position
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            // Mouse drag event
            public void mouseDragged(MouseEvent e) {
                Point point = e.getPoint(); // Get current position
                Point locationPoint = getLocation(); // Get window location
                int x = locationPoint.x + point.x - pressedPoint.x; // Compute the new position after dragging
                int y = locationPoint.y + point.y - pressedPoint.y;
                setLocation(x, y);// Change the position of window
            }
        });

        this.setTitle("");
//        this.setBounds(100, 100, 354, 206);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
