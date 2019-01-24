package com.tapereader.gui.chart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

public class TRChartPanel extends JPanel {
    
    public TRChartPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 700));
    }

}
