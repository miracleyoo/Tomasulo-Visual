package com.miracleyoo.utils;

import com.miracleyoo.UIs.DataUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.crypto.Data;
import java.awt.*;

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

    public static class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //To highlight table components for the cycle graph, see below

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            //Get the status for the current row.
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

            Color background_color = Color.WHITE;
//            String[] tempColorScheme = DataUI.colorSchemeCycleCur;//DataUI.DarkMode? DataUI.colorSchemeCycleDark:DataUI.colorSchemeCycleLight;
            if(tableModel.getValueAt(row, col) != null) {
                background_color = Color.decode(DataUI.colorSchemeCycleCur[Integer.parseInt((String)tableModel.getValueAt(row, 1))%DataUI.colorSchemeCycleCur.length]);

//                switch ((String) tableModel.getValueAt(row, col)) {
//                    case "IF":
//                        background_color = Color.decode(DataUI.colorSchemeCycleCur[0]);
//                        break;
//                    case "ID":
//                        background_color = Color.decode(DataUI.colorSchemeCycleCur[1]);
//                        break;
//                    case "EX":
//                        background_color = Color.decode(DataUI.colorSchemeCycleCur[2]);
//                        break;
//                    case "MEM":
//                        background_color = Color.decode(DataUI.colorSchemeCycleCur[3]);
//                        break;
//                    case "WB":
//                        background_color = Color.decode(DataUI.colorSchemeCycleCur[4]);
//                        break;
//                }
            }
            else{
//                if(DataUI.DarkMode){
                background_color = Color.decode(DataUI.colorSchemeMainCur[0]);
//                }
//                else {
//                    background_color = Color.decode(DataUI.colorSchemeMainLight[1]);
//                }
            }
            l.setBackground(background_color);

            //Return the JLabel which renders the cell.
            return l;
        }
    }
}

