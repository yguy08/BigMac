package com.tapereader.tip.selllow;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.tip.SwingTip;

public class SellLow extends SwingTip {

    public Strategy buildStrategy(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        String tipName = "Sell Low";
        // Going long if the close price goes above the max
        Rule entryRule = new IsHighestRule(closePrices, 25);
        // Exit if the close price goes below the min or stop loss
        Rule exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
        return new BaseStrategy(tipName, entryRule, exitRule, 25);
    }

}
