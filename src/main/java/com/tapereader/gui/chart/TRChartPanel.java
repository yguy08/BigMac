package com.tapereader.gui.chart;

import java.awt.Color;

import javax.swing.UIManager;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class TRChartPanel extends ChartPanel {

    public TRChartPanel(JFreeChart chart) {
        super(chart);
        setFillZoomRectangle(true);
        setMouseWheelEnabled(true);
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setZoomOutlinePaint(Color.GREEN);
        setDisplayToolTips(true);
        chart.addChangeListener(this);
    }
    
    public void rebuildChart(JFreeChart chart) {
        setChart(chart);
        chart.fireChartChanged();
    }

}
