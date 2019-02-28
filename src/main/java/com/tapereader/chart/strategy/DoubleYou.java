package com.tapereader.chart.strategy;

import org.ta4j.core.Order.OrderType;
import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.IsHighestRule;

import com.tapereader.enumeration.TipType;

/**
 * For now just a copy of Buy High
 */
public class DoubleYou extends ChartStrategyAbs {

    public DoubleYou(TimeSeries series) {
        super(series);
    }

    @Override
    public Rule getEntryRule() {
        return new IsHighestRule(closePrices, 25);
    }

    @Override
    public Rule getExitRule() {
        return new IsHighestRule(closePrices, 25);
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.BUY;
    }

    /**
     * @return the closePrices
     */
    public ClosePriceIndicator getClosePrices() {
        return closePrices;
    }

    /**
     * @param closePrices the closePrices to set
     */
    public void setClosePrices(ClosePriceIndicator closePrices) {
        this.closePrices = closePrices;
    }

    @Override
    public String getName() {
        return TipType.DOUBLE_U.getDisplayName();
    }

}
