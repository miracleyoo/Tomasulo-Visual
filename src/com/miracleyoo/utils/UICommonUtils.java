package com.miracleyoo.utils;

import javax.swing.*;
import java.awt.*;

public class UICommonUtils {
    public static void makeFrameToCenter(JFrame frame) {
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        Dimension thisSize=frame.getSize();
        if(thisSize.width>screenSize.width){
            thisSize.width=screenSize.width;
        }
        if(thisSize.height>screenSize.height){
            thisSize.height=screenSize.height;
        }
//        frame.setSize(1000, 700);
        frame.setLocation((screenSize.width - thisSize.width) / 2, (screenSize.height - thisSize.height) / 2);
    }
}
