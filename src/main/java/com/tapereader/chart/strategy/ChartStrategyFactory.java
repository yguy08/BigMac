package com.tapereader.chart.strategy;

import com.tapereader.chart.strategy.buyhigh.BuyHigh;
import com.tapereader.chart.strategy.buylow.BuyLow;
import com.tapereader.enumeration.TipType;

public class ChartStrategyFactory {
    public static ChartStrategy buildChartStrategy(TipType tip) {
        switch(tip) {
        case BUY_HIGH:
            return new BuyHigh();
        case BUY_LOW:
            return new BuyLow();
        default:
            return new BuyHigh();
        }
    }
}
