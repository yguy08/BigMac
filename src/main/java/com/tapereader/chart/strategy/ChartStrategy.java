package com.tapereader.chart.strategy;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.TimeSeries;

public interface ChartStrategy {
    
    public JFreeChart buildChart(TimeSeries series);
    
    public void addBuySellSignalsToChart();
    
    public String getStrategyAnalysis();
}
