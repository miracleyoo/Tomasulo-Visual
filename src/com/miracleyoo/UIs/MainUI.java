package com.miracleyoo.UIs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.miracleyoo.utils.*;

public class MainUI {
    private JButton ChooseFileBtn;
    private JPanel PanelMain;
    private JLabel FileSelectedCap;
    private String selectedFileName;
    private Map< String, List<Object[]>> listFlagMap;

//    static private JLabel backgroundLabel;
    static private JFrame frame = new NoneFrame();
    static int[] frameSize= new int[]{500,200};

    private MainUI() {
        assert ChooseFileBtn != null;
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
    }

    public static void main(String[] args){
        MainUI mainUI = new MainUI();
        frame.setContentPane(mainUI.PanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(frame);
//        frame.pack();
        frame.setVisible(true);
    }
}
