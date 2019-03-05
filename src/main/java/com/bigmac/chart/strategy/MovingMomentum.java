package com.bigmac.chart.strategy;

import org.ta4j.core.Order.OrderType;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import com.bigmac.enumeration.TipType;

import org.ta4j.core.Rule;
import org.ta4j.core.TimeSeries;

public class MovingMomentum extends ChartStrategyAbs {

    public MovingMomentum(TimeSeries series) {
        super(series);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Rule getEntryRule() {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);

        StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(series, 14);

        MACDIndicator macd = new MACDIndicator(closePrice, 9, 26);
        EMAIndicator emaMacd = new EMAIndicator(macd, 18);
        // Entry rule
        Rule entryRule = new OverIndicatorRule(shortEma, longEma) // Trend
                .and(new CrossedDownIndicatorRule(stochasticOscillK, 20)) // Signal 1
                .and(new OverIndicatorRule(macd, emaMacd)); 
        return entryRule;
    }

    @Override
    public Rule getExitRule() {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);

        StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(series, 14);

        MACDIndicator macd = new MACDIndicator(closePrice, 9, 26);
        EMAIndicator emaMacd = new EMAIndicator(macd, 18);
        // Entry rule
        Rule entryRule = new OverIndicatorRule(shortEma, longEma) // Trend
                .and(new CrossedDownIndicatorRule(stochasticOscillK, 20)) // Signal 1
                .and(new OverIndicatorRule(macd, emaMacd)); 
        // Exit rule
        Rule exitRule = new UnderIndicatorRule(shortEma, longEma) // Trend
                .and(new CrossedUpIndicatorRule(stochasticOscillK,20)) // Signal 1
                .and(new UnderIndicatorRule(macd, emaMacd)); // Signal 2
        return exitRule;
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.BUY;
    }

    @Override
    public String getName() {
        return TipType.MOVING_MOMENTUM.getDisplayName();
    }

}
