package com.tapereader.tip;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.google.common.eventbus.Subscribe;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.gui.chart.ChartUtils;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketData;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Security;
import com.tapereader.model.Tip;
import com.tapereader.tip.TapeReader;

public class SwingTip extends TapeReader {

    private Map<String, Tick> tickMap = new ConcurrentHashMap<>();

    @Override
    public void init() {
        List<Tick> ticks = getMarketDataClerk().getCurrentTicks();
        for (Tick t : ticks) {
            tickMap.put(t.getSymbol(), t);
        }
    }

    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = tickMap.values().stream()
                .filter(t -> t.getTimestamp() > Instant.now().minusSeconds(86400).toEpochMilli())
                .collect(Collectors.toList());
        return ticks;
    }

    public List<MarketType> getAllMarkets() {
        return Arrays.asList(MarketType.values());
    }

    public void storeHistoricalBars(Security security, Instant start, Instant now, Duration ofDays) {
        List<Bar> bars = getHistoricalDataClerk().getHistoricalBars(security, start, now, ofDays);
        getRecordClerk().updateBars(security, bars);
    }

    public List<Bar> getBars(Security security) {
        return getLookupClerk().getBars(security);
    }

    public List<Tip> getAllTips() {
        return getLookupClerk().getAllTips();
    }

    public Security getSecurity(String symbol, TickerType tickerType) {
        return getLookupClerk().findSecurity(symbol, tickerType);
    }

    public List<BucketShop> getAllBucketShops() {
        return getLookupClerk().getAllBucketShops();
    }

    public TimeSeries buildTimeSeries(List<Bar> bars) {
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().build();
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        return series;
    }

    public Strategy buildStrategy(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        String tipName = getTip().getName();

        // Going long if the close price goes above the max
        Rule entryRule;
        // Exit if the close price goes below the min or stop loss
        Rule exitRule;

        switch (tipName) {
        case "Buy High":
            entryRule = new IsHighestRule(closePrices, 25);
            exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
            break;
        case "Sell Low":
            entryRule = new IsLowestRule(closePrices, 25);
            exitRule = new IsHighestRule(closePrices, 11);
            break;
        default:
            entryRule = new IsHighestRule(closePrices, 25);
            exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
            break;
        }
        return new BaseStrategy(tipName, entryRule, exitRule, 25);
    }

    public void addBuySellSignals(TimeSeries series, Strategy strategy, JFreeChart chart) {
        String strategyName = strategy.getName();
        OrderType type;
        switch (strategyName) {
        case "Buy High":
            type = OrderType.BUY;
            break;
        case "Sell Low":
            type = OrderType.SELL;
            break;
        default:
            type = OrderType.BUY;
            break;
        }
        ChartUtils.addBuySellSignals(series, strategy, chart, type);
    }

    @Subscribe
    public void onMarketData(MarketData data) {
        if (data instanceof Tick) {
            Tick tick = (Tick) data;
            tickMap.put(tick.getSymbol(), tick);
        }
        System.out.println("Tick updated: " + data);
    }
}
