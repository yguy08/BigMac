package com.tapereader.chart;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.TimeSeries;

import com.tapereader.chart.strategy.ChartStrategy;

public class ChartManager {

    private TimeSeries series;

    public ChartManager() {

    }

    public void setTimeSeries(TimeSeries series) {
        this.series = series;
    }

    public JFreeChart buildChart(ChartStrategy chartStrategy) {
        return chartStrategy.buildChart(series);
    }
    
    public void addBuySellSignalsToChart(ChartStrategy chartStrategy) {
        chartStrategy.addBuySellSignalsToChart();
    }
    
    public String getStrategyAnalysis(ChartStrategy chartStrategy) {
        return chartStrategy.getStrategyAnalysis();
    }

}
