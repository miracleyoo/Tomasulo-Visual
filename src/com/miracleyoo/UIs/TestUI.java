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

public class TestUI {
    private JButton ChooseFileBtn;
    private JButton ExitBtn;
    private BackgroundPanel panel;
    private JLabel FileSelectedCap;
    private String selectedFileName;
    private Map< String, List<Object[]>> listFlagMap;
    private static JFrame frame = new NoneFrame();
    private static int[] frameSize= new int[]{500,250};

    private TestUI() throws IOException {
        BufferedImage img = null;
        img = ImageIO.read(new File("Assets/image_01.jpg"));
        panel = new BackgroundPanel(img, BackgroundPanel.SCALED, 1.0f, 0.5f);
        GradientPaint paint = new GradientPaint(0, 0, Color.BLUE, 600, 0, Color.RED);
        panel.setPaint(paint);

        FileSelectedCap = new JLabel("<html><font color='white'>Welcome! Please select a *.s file to start!</font></html>");
        FileSelectedCap.setHorizontalAlignment(SwingConstants.CENTER);
        FileSelectedCap.setVerticalAlignment(SwingConstants.CENTER);
        FileSelectedCap.setFont(new Font("Dialog", Font.BOLD, 20));

        ChooseFileBtn = new JButton();
        ChooseFileBtn.setText("<html><font color='white'>Select *.s File</font></html>");
        ChooseFileBtn.setOpaque(false);
        ChooseFileBtn.setContentAreaFilled(false);
        ChooseFileBtn.setBorderPainted(false);

        ExitBtn = new JButton();
        ExitBtn.setText("<html><font color='white'>X</font></html>");
        ExitBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        ExitBtn.setOpaque(false);
        ExitBtn.setContentAreaFilled(false);
        ExitBtn.setBorderPainted(false);


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
                    selectedFileName = filePath+fileName;
                    listFlagMap = ParseFile.parseFile(new File(selectedFileName));
                    Object[][] operandListArray = listFlagMap.get("textList").toArray(new Object[0][0]);
                    Object[][] dataListArray = listFlagMap.get("dataList").toArray(new Object[0][0]);
                    new DataUI(operandListArray, dataListArray);
                    frame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
         ExitBtn.addActionListener(new ActionListener(){
             @Override
             public void actionPerformed(ActionEvent e) {
                System.exit(0);
             }
         });
    }

    public static void main(String[] args) throws IOException {


        TestUI testUI = new TestUI();
        frame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(frame);

        frame.setContentPane(testUI.panel);
        frame.getContentPane().add(testUI.FileSelectedCap);
        frame.getContentPane().setLayout(null);

        testUI.FileSelectedCap.setBounds(0, 10, frame.getWidth()-20, 30);
        testUI.ExitBtn.setBounds(frame.getWidth()-20, 0, 20, 15);
        testUI.ChooseFileBtn.setBounds(0, frame.getHeight()-60, frame.getWidth(), 60);

        frame.getContentPane().add(testUI.ChooseFileBtn);
        frame.getContentPane().add(testUI.ExitBtn);

        // Add menu bar to frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}
