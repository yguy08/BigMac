package com.tapereader.gui.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TRTable extends JTable {
    
    /**
     * Default Serial UID
     */
    private static final long serialVersionUID = -2086402147590791438L;
    
    private TableRowSorter<TableModel> sorter;

    public TRTable(TableModel tableModel) {
        super(tableModel);
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(true);
        getTableHeader().setResizingAllowed(true);
        getTableHeader().setEnabled(true);
        ((JLabel)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        sorter = new TableRowSorter<TableModel>(tableModel);
        setRowSorter(sorter);
    }
    
    public void setFilter(String filter) {
        String re1=".*?";   // Non-greedy match on filler
        String re2="(\\/)"; // Any Single Character 1
        String re3=filter;
        String regExTxt = re1+re2+re3;
        RowFilter<TableModel, Integer> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(regExTxt, 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
        sort(2, SortOrder.DESCENDING);
    }
    
    private void sort(int columnIndex, SortOrder sortOrder) {
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(columnIndex, sortOrder));
        this.getRowSorter().setSortKeys(sortKeys);
        ((DefaultRowSorter) this.getRowSorter()).sort();
    }
}
