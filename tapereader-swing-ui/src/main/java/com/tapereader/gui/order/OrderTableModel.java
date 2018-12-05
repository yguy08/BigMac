package com.tapereader.gui.order;

import javax.swing.table.AbstractTableModel;

public class OrderTableModel extends AbstractTableModel {
    
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -6778455750154890852L;

    private String[] columnNames = { "Tip", "Symbol", "Side", "Quantity", "Status", "Filled Quantity", "Progress" };

    
    OrderTableModel() {
        
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return 5;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        try {
            switch(col) {
            case 0:
                return " ";
            case 1:
                return " ";
            case 2:
                return " ";
            case 3:
                return " ";
            case 4:
                return " ";
            default:
                return " ";
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class<?> getColumnClass(int c) {
        try {
            switch(c) {
            case 0:
                return String.class;
            case 1:
                return Number.class;
            case 2:
                return Integer.class;
            default:
                return String.class;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return String.class;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        
    }
}
