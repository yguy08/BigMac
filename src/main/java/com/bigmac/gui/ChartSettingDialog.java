package com.bigmac.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChartSettingDialog extends JDialog {
    
    public ChartSettingDialog(Frame aFrame) {
        super(aFrame, "Chart Settings", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setLocationRelativeTo(aFrame);

        JLabel label = new JLabel("Chart Settings");
        label.setHorizontalAlignment(JLabel.CENTER);
        Font font = label.getFont();
        label.setFont(label.getFont().deriveFont(font.PLAIN,
                                                 14.0f));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        JPanel closePanel = new JPanel();
        closePanel.setLayout(new BoxLayout(closePanel,
                                           BoxLayout.LINE_AXIS));
        closePanel.add(Box.createHorizontalGlue());
        closePanel.add(closeButton);
        closePanel.setBorder(BorderFactory.
            createEmptyBorder(0,0,5,5));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(label, BorderLayout.CENTER);
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

}
