package com.tapereader.gui.position;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import com.tapereader.gui.TapeReaderGuiMain;
import com.tapereader.model.Line;

public class PositionPanel extends JPanel {

    private static final long serialVersionUID = -179869562891160216L;
    
    private PositionTable positionTable;
    
    private JButton addButton;
    
    private List<Line> positions;
    
    public PositionPanel(List<Line> positions) {
        this.positions = positions;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(450, 250));
        
        add(buildTitlePanel());
        
        positionTable = new PositionTable(new PositionTableModel(positions));
        JScrollPane scrollPane = new JScrollPane(positionTable);
        add(scrollPane);
    }
    
    public JPanel buildTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
        JLabel positionLbl = new JLabel("Position");
        titlePanel.add(positionLbl);
        titlePanel.add(Box.createRigidArea(new Dimension(40,5)));
        addButton = new JButton("Add");
        titlePanel.add(addButton);
        titlePanel.add(Box.createRigidArea(new Dimension(5,5)));
        return titlePanel;
    }

}
