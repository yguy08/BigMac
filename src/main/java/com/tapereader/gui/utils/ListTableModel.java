package com.tapereader.gui.utils;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ListTableModel extends AbstractTableModel {
    
    private List<?> elements;
    
    private TableModelMapper mapper;
    
    public ListTableModel(TableModelMapper mapper) {
        this.mapper = mapper;
    }
    
    @Override
    public Class getColumnClass(int col) {
        return mapper.getColumnClass(col);
    }

    @Override
    public int getRowCount() {
        return elements.size();
    }

    @Override
    public int getColumnCount() {
        return mapper.getColumnCount();
    }
    
    @Override
    public String getColumnName(int col) {
        return mapper.getColumnName(col);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return mapper.getValueAt(elements.get(rowIndex), columnIndex);
    }
    
    public void setElements(List<?> elements) {
        this.elements = elements;
    }
    
    public List<?> getElements() {
        return elements;
    }

}
