package com.tapereader.gui.transaction;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TransactionPanel extends JPanel {

    private static final long serialVersionUID = -179869562891160216L;
    
    public TransactionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setOpaque(true);
        setPreferredSize(new Dimension(400, 250));
        
        JLabel lbl = new JLabel("Transaction");
        add(lbl);
        
        TransactionTable marketTable = new TransactionTable(new TransactionTableModel());
        JScrollPane scrollPane = new JScrollPane(marketTable);
        add(scrollPane);
    }
    
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MarketDataPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Set up the content pane.
        frame.getContentPane().add(new TransactionPanel());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
