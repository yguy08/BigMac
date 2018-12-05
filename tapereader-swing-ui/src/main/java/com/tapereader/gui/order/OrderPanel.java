package com.tapereader.gui.order;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class OrderPanel extends JPanel {

    private static final long serialVersionUID = -179869562891160216L;

    public OrderPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setOpaque(true);
        setPreferredSize(new Dimension(450, 250));

        JLabel lbl = new JLabel("Order");
        add(lbl);

        OrderTable marketTable = new OrderTable(new OrderTableModel());
        JScrollPane scrollPane = new JScrollPane(marketTable);
        add(scrollPane);
    }

}
