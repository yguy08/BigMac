package com.tapereader.chart;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.TimeSeries;
import com.tapereader.chart.strategy.ChartStrategy;

public class ChartManager {

    private ChartStrategy chartStrategy;

    public ChartManager(ChartStrategy chartStrategy) {
        this.chartStrategy = chartStrategy;
    }

    public void changeTimeSeries(TimeSeries series) {
        chartStrategy.setSeries(series);
    }

    public void changeChartStrategy(ChartStrategy chartStrategy) {
        this.chartStrategy = chartStrategy;
    }
    
    public String getStrategyAnalysis() {
        return chartStrategy.getStrategyAnalysis();
    }
    
    /**
     * @param chartStrategy the chartStrategy to set
     */
    public ChartStrategy getChartStrategy() {
        return chartStrategy;
    }

    public JFreeChart buildChart() {
        return chartStrategy.buildChart();
    }

}
