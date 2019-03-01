package com.bigmac.chart.strategy;

import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.bigmac.enumeration.TipType;

public class BuyLow extends ChartStrategyAbs {

    public BuyLow(TimeSeries series) {
        super(series);
    }

    @Override
    public Rule getEntryRule() {
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        return new IsLowestRule(closePrices, 55);
    }

    @Override
    public Rule getExitRule() {
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        return new IsHighestRule(closePrices, 55).or(new StopLossRule(closePrices, PrecisionNum.valueOf(50)));
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.BUY;
    }

    @Override
    public String getName() {
        return TipType.BUY_LOW.toString();
    }

}
