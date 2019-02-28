package com.tapereader.chart.strategy;

import org.ta4j.core.TimeSeries;

import com.tapereader.enumeration.TipType;

public class ChartStrategyFactory {
    
    public static ChartStrategy buildChartStrategy(TipType tip, TimeSeries series) {
        switch (tip) {
        case BUY_HIGH:
            return new BuyHigh(series);
        case BUY_LOW:
            return new BuyLow(series);
        case DOUBLE_U:
            return new DoubleYou(series);
        default:
            return new BuyHigh(series);
        }
    }

}
