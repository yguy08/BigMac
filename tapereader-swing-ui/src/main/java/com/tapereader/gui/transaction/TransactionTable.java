package com.tapereader.gui.transaction;

import javax.swing.JFrame;
import com.tapereader.gui.TrTable;

public class TransactionTable extends TrTable {
    
    /**
     * Default Serial UID
     */
    private static final long serialVersionUID = -2086402147590791438L;

    public TransactionTable(TransactionTableModel tableModel) {
        super(tableModel);
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("DopeBookTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
