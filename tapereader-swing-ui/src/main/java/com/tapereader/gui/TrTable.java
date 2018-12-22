package com.tapereader.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;


public class TrTable extends JTable {

    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -6947325578748586330L;
    
    public TrTable(TableModel tableModel) {
        super(tableModel);
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(true);
        getTableHeader().setResizingAllowed(true);
        getTableHeader().setEnabled(true);
        ((JLabel)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        //setPreferredScrollableViewportSize(new Dimension(350, 200));
    }
    
    public void sortTable(int columnIndex, SortOrder sortOrder) {
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(columnIndex, sortOrder));
        this.getRowSorter().setSortKeys(sortKeys);
        ((DefaultRowSorter) this.getRowSorter()).sort();
    }

}
