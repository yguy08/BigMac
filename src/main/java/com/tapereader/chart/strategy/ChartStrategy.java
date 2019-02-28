package com.tapereader.chart.strategy;

import java.util.List;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.Order.OrderType;

public interface ChartStrategy {
    
    public Rule getEntryRule();
    
    public Rule getExitRule();
    
    public TimeSeries getSeries();

    public void setSeries(TimeSeries series);
    
    public OrderType getOrderType();
    
    public TradingRecord getTradingRecord();
    
    public String getStrategyAnalysis();
    
    public String getName();
}
