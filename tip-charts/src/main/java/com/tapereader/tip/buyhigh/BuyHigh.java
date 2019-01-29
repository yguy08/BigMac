package com.tapereader.tip.buyhigh;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.gui.chart.ChartUtils;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;
import com.tapereader.tip.SwingTip;

public class BuyHigh extends SwingTip {
    
    public BuyHigh() {
        setTipName("Buy High");
    }
    
    public JFreeChart buildJFreeChart(Security security, Instant start, Duration barSize) {
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().build();
        List<Bar> bars = getHistoricalDataClerk()
                .getHistoricalBars(security, start, Instant.now(), barSize);
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        // Going long if the close price goes above the max
        Rule entryRule = new IsHighestRule(closePrices, 25);
        // Exit if the close price goes below the min or stop loss
        Rule exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
        Strategy strategy = new BaseStrategy(getTipName(), entryRule, exitRule, 25);
        
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(security.getSymbol(), series);
        ChartUtils.addBuySellSignals(series, strategy, chart, OrderType.BUY);
        return chart;
    }

}
