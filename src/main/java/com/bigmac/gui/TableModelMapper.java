package com.bigmac.gui;

public interface TableModelMapper {
    
    public int getColumnCount();
    
    public String getColumnName(int col);
    
    public Object getValueAt(Object row, int col);
    
    public Class<?> getColumnClass(int c);
}
