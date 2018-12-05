package com.tapereader.gui.position;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.tapereader.model.Line;

public class PositionTableModel extends AbstractTableModel {
    
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -6778455750154890852L;

    private String[] columnNames = { "Symbol", "Quantity", "Price", "Last", "Value", "Cost", " UnrealizedPL", "RealizedPL" };

    private List<Line> positions;

    PositionTableModel(List<Line> positions) {
        this.positions = positions;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return positions.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Line p = positions.get(row);
        try {
            switch(col) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                return " ";
            }
        } catch (Exception e) {
            return "ERROR";
        }
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
