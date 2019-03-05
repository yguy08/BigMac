package com.bigmac.chart.strategy;

import org.ta4j.core.Order.OrderType;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;

import com.bigmac.enumeration.TipType;

/**
 * For now just a copy of Buy High
 */
public class DoubleYou extends ChartStrategyAbs {

    public DoubleYou(TimeSeries series) {
        super(series);
    }

    @Override
    public Rule getEntryRule() {
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        return new IsHighestRule(closePrices, 25);
    }

    @Override
    public Rule getExitRule() {
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        return new IsLowestRule(closePrices, 11);
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.BUY;
    }

    @Override
    public String getName() {
        return TipType.DOUBLE_U.getDisplayName();
    }

}
