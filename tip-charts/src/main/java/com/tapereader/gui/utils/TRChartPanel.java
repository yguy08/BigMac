package com.tapereader.gui.utils;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class TRChartPanel extends ChartPanel {

    public TRChartPanel(JFreeChart chart) {
        super(chart);
        setFillZoomRectangle(true);
        setMouseWheelEnabled(true);
        setOpaque(false);
        setZoomOutlinePaint(Color.GREEN);
        setDisplayToolTips(true);
    }

}
