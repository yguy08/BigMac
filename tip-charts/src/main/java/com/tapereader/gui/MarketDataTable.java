package com.tapereader.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

import com.tapereader.marketdata.Tick;

public class MarketDataTable extends JTable {
    
    /**
     * Default Serial UID
     */
    private static final long serialVersionUID = -2086402147590791438L;
    
    private TableRowSorter<MarketDataTableModel> sorter;

    public MarketDataTable(MarketDataTableModel tableModel) {
        super(tableModel);
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(true);
        getTableHeader().setResizingAllowed(true);
        getTableHeader().setEnabled(true);
        ((JLabel)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        addMouseListener(new MarketDataTableListener());
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
    
    public void sortTable(int columnIndex, SortOrder sortOrder) {
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(columnIndex, sortOrder));
        this.getRowSorter().setSortKeys(sortKeys);
        ((DefaultRowSorter) this.getRowSorter()).sort();
    }
    
    public void updateTicks(List<Tick> ticks) {
        SwingUtilities.invokeLater(() -> {
            ((MarketDataTableModel) getModel()).setTicks(ticks);
            ((MarketDataTableModel) getModel()).fireTableStructureChanged();
        });
    }
    
    private class MarketDataTableListener extends MouseAdapter {
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() > 1) {
                List<Tick> ticks = ((MarketDataTableModel) getModel()).getTicks();
                int i = convertRowIndexToModel(getSelectedRow());
                Tick tick = ticks.get(i);
                //TRGuiMain.getTrGui().rebuildChartPanel(tick.getSymbol());
            }
        }
    }
}
