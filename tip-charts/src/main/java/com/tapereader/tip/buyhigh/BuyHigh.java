package com.tapereader.tip.buyhigh;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.enumeration.TipType;
import com.tapereader.gui.utils.ChartUtils;
import com.tapereader.tip.Tip;

public class BuyHigh implements Tip {

    public JFreeChart buildJFreeChart(String symbol, TimeSeries series) {
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        // Going long if the close price goes above the max
        Rule entryRule = new IsHighestRule(closePrices, 25);
        // Exit if the close price goes below the min or stop loss
        Rule exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
        Strategy strategy = new BaseStrategy(TipType.BUY_HIGH.toString(), entryRule, exitRule, 25);
        
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(symbol, series);
        ChartUtils.addBuySellSignals(series, strategy, chart, OrderType.BUY);
        return chart;
    }

}
