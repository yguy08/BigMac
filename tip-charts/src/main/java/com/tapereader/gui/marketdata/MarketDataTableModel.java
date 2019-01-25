package com.tapereader.gui.marketdata;

import java.util.List;
import java.util.function.Function;

import javax.swing.table.AbstractTableModel;

import com.tapereader.marketdata.Tick;

public class MarketDataTableModel extends AbstractTableModel {
    
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -6778455750154890852L;

    public String[] columnNames = {"Symbol","Last","Volume"};

    private List<Tick> ticks;
    
    public MarketDataTableModel(List<Tick> ticks) {
        this.ticks = ticks;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return ticks.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Tick tick = ticks.get(row);
        try {
            switch(col) {
            case 0:
                return tick.getSymbol();
            case 1:
                return DECIMALFUNC.apply(tick);
            case 2:
                return tick.getVolume();
            default:
                return "ERROR";
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "ERROR";
    }
    
    private Function<Tick, String> DECIMALFUNC = t -> {
        return t.getSymbol().endsWith("USDT") ? String.format("%.2f", t.getLast()) :
            String.format("%.8f", t.getLast());
    };

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
        ticks.get(row).set((Tick) value);
        fireTableCellUpdated(row, col);
    }
    
    public List<Tick> getTicks() {
        return ticks;
    }
    
    public void setTicks(List<Tick> ticks) {
        this.ticks = ticks;
    }
}
