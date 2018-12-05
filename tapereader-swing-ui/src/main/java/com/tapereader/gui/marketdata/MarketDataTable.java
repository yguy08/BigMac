package com.tapereader.gui.marketdata;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.tapereader.gui.TapeReaderGuiMain;
import com.tapereader.gui.TrTable;
import com.tapereader.marketdata.Tick;

public class MarketDataTable extends TrTable {
    
    /**
     * Default Serial UID
     */
    private static final long serialVersionUID = -2086402147590791438L;
    
    private TableRowSorter<MarketDataTableModel> sorter;

    public MarketDataTable(MarketDataTableModel tableModel) {
        super(tableModel);
        addMouseListener(new MarketDataTableListener());
        //getModel().addTableModelListener(new MarketDataTableModelListener());
        sorter = new TableRowSorter<MarketDataTableModel>(tableModel);
        setRowSorter(sorter);
    }
    
    public void setFilter(String marketName, String tickerType) {
        String re1=".*?";   // Non-greedy match on filler
        String re2="(\\/)"; // Any Single Character 1
        String re3=marketName; // Word 1
        String re4="(:)";   // Any Single Character 2
        String re5=tickerType; // Word 2
        String regExTxt = re1+re2+re3+re4+re5;
        RowFilter<MarketDataTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(regExTxt, 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
        sortTable(2, SortOrder.DESCENDING);
    }
    
    public void updateTicks(List<Tick> ticks) {
        SwingUtilities.invokeLater(() -> {
            ((MarketDataTableModel) getModel()).setTicks(ticks);
            ((MarketDataTableModel) getModel()).fireTableStructureChanged();
        });
    }
    
    private class MarketDataTableListener extends MouseAdapter implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() > 1) {
                List<Tick> ticks = ((MarketDataTableModel) getModel()).getTicks();
                int i = convertRowIndexToModel(getSelectedRow());
                Tick tick = ticks.get(i);
                TapeReaderGuiMain.getTrGui().rebuildChart(tick.getSymbol());
            }
        }
    }
    
    private class MarketDataTableModelListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel)e.getSource();
            String columnName = model.getColumnName(column);
            Object data = model.getValueAt(row, column);
            System.out.println("CHANGED: " + data);
        }
    }
}