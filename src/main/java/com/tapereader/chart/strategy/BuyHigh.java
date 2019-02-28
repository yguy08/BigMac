package com.tapereader.chart.strategy;

import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.enumeration.TipType;

public class BuyHigh extends ChartStrategyAbs {
    
    public BuyHigh(TimeSeries series) {
        super(series);
    }

    @Override
    public Rule getEntryRule() {
        return new IsHighestRule(closePrices, 25);
    }

    @Override
    public Rule getExitRule() {
        return new IsLowestRule(closePrices, 11)
                .or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.BUY;
    }

    @Override
    public String getName() {
        return TipType.BUY_HIGH.toString();
    }

}
