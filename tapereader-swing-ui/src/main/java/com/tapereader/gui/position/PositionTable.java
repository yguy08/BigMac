package com.tapereader.gui.position;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.tapereader.gui.TrTable;

public class PositionTable extends TrTable {
    
    /**
     * Default Serial UID
     */
    private static final long serialVersionUID = -2086402147590791438L;

    public PositionTable(PositionTableModel tableModel) {
        super(tableModel);
        addMouseListener(new PositionTableListener());
    }
    
    private class PositionTableListener extends MouseAdapter implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() > 1) {
                
            }
        }
    }
}
