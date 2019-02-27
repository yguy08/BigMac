package com.tapereader.chart.strategy;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;

public interface ChartStrategy {
    
    public Rule getEntryRule(TimeSeries series);
    
    public Rule getExitRule(TimeSeries series);
    
    public JFreeChart buildChart(TimeSeries series);
    
    public void addBuySellSignalsToChart();
    
    public String getStrategyAnalysis();
}
