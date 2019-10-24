package com.miracleyoo.utils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableUtils {
    public static void setOneColumnSize(JTable table, int i, int preferedWidth, int maxWidth, int minWidth) {
        // The column model of the table
        TableColumnModel cm = table.getColumnModel();
        // Get the i-th column

        TableColumn column = cm.getColumn(i);
        column.setPreferredWidth(preferedWidth);
        column.setMaxWidth(maxWidth);
        column.setMinWidth(minWidth);
    }

    public static void setAllPreferredColumnSize(JTable table, int[] Widths) {
        int i = 0;
        for (int width:Widths){
            TableColumnModel cm = table.getColumnModel();
            TableColumn column = cm.getColumn(i);
            column.setPreferredWidth(width);
            i++;
        }
    }

    public static void setAllMinColumnSize(JTable table, int[] Widths) {
        int i = 0;
        for (int width:Widths){
            TableColumnModel cm = table.getColumnModel();
            TableColumn column = cm.getColumn(i);
            column.setMinWidth(width);
            i++;
        }
    }
}

