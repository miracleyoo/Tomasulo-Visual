/**
 * Functionality: A simple welcome interface. It will ask user to select
 * a *.s file and parse the file into a list of map. Each line will be
 * put into the corresponding array as a item.
 * */

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
    private JButton ChooseFileBtn;    // The button used to choose a *.s file.
    private JPanel PanelMain;         // The main panel.
    private JLabel FileSelectedCap;   // Welcome label.
    private String selectedFileName;  // The name of the user selected file.
    private Map< String, List<Object[]>> listFlagMap; // The result of parse file.

    static private JFrame frame = new NoneFrame(); // The main frame.
    static int[] frameSize= new int[]{500,200};  // The main frame size.

    private MainUI() {
        assert ChooseFileBtn != null;
        // Choose file and parse it here
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
                    selectedFileName = filePath + fileName;
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

    // Main function
    public static void main(String[] args) {
        MainUI mainUI = new MainUI();
        frame.setContentPane(mainUI.PanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameSize[0], frameSize[1]);
        UICommonUtils.makeFrameToCenter(frame);
        frame.setVisible(true);
    }
}
