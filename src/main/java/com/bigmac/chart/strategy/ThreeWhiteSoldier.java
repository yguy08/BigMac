package com.bigmac.chart.strategy;

import org.ta4j.core.Order.OrderType;
import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.candles.ThreeWhiteSoldiersIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.IsEqualRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.bigmac.enumeration.TipType;

public class ThreeWhiteSoldier extends ChartStrategyAbs {

    public ThreeWhiteSoldier(TimeSeries series) {
        super(series);
    }

    @Override
    public Rule getEntryRule() {
        ThreeWhiteSoldiersIndicator white = new ThreeWhiteSoldiersIndicator(series, 10, PrecisionNum.valueOf(1));
        return new BooleanIndicatorRule(white);
    }

    @Override
    public Rule getExitRule() {
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        return new IsLowestRule(closePrices, 11)
                .or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
    }

    @Override
    public OrderType getOrderType() {
        // TODO Auto-generated method stub
        return OrderType.BUY;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return TipType.WHITE_SOLDIER.getDisplayName();
    }

}
