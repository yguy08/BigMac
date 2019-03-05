package com.bigmac.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.bigmac.config.ChartConfig;

public class ChartSettingDialog extends JDialog implements ItemListener {
    
    // checkbox
    JCheckBox includeZeroButton;
    JCheckBox addSMAButton;
    
    public ChartSettingDialog(Frame aFrame) {
        super(aFrame, "Chart Settings", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setLocationRelativeTo(aFrame);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        JPanel closePanel = new JPanel();
        closePanel.add(closeButton);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(buildChartOptionPanel());
        contentPane.add(closePanel, BorderLayout.PAGE_END);
        contentPane.setOpaque(true);
        setContentPane(contentPane);

        //Show it.
        setPreferredSize(new Dimension(300, 150));
        setLocationRelativeTo(aFrame);
    }
    
    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        setVisible(false);
    }
    
    private JPanel buildChartOptionPanel() {
        //Create the check boxes.
        includeZeroButton = new JCheckBox("Include 0");
        includeZeroButton.setSelected(ChartConfig.getIncludeZero());
        
        //Create the check boxes.
        addSMAButton = new JCheckBox("Add SMA");
        addSMAButton.setSelected(ChartConfig.isAddSMA());
 
        //Register a listener for the check boxes.
        includeZeroButton.addItemListener(this);
        addSMAButton.addItemListener(this);
 
        //Put the check boxes in a column in a panel
        JPanel checkPanel = new JPanel();
        checkPanel.add(includeZeroButton);
        checkPanel.add(addSMAButton);
        return checkPanel;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        boolean selected = e.getStateChange() == ItemEvent.SELECTED;
        if (source == includeZeroButton) {
            ChartConfig.setIncludeZero(selected);
        } else if (source == addSMAButton) {
            ChartConfig.setAddSMA(selected);
        }
    }

}
