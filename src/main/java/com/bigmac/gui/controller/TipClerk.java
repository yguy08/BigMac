package com.bigmac.gui.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import com.bigmac.chart.strategy.ChartStrategy;
import com.bigmac.config.Config;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;
import com.bigmac.marketdata.MarketDataClerk;
import com.bigmac.marketdata.Tick;
import com.bigmac.marketdata.cache.MarketDataCacheClerk;
import com.bigmac.marketdata.historical.HistoricalDataClerk;

public class TipClerk {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TipClerk.class);
    
    private final Config config;
    
    private final MarketDataClerk marketDataClerk;
    
    private final MarketDataCacheClerk cacheClerk;
    
    private final HistoricalDataClerk historicalDataClerk;
    
    private ChartStrategy chartStrategy;
    
    public TipClerk(Config config, MarketDataClerk marketDataClerk, HistoricalDataClerk historicalDataClerk, 
            MarketDataCacheClerk cacheClerk, ChartStrategy chartStrategy) {
        this.config = config;
        this.marketDataClerk = marketDataClerk;
        this.cacheClerk = cacheClerk;
        this.historicalDataClerk = historicalDataClerk;
        this.chartStrategy = chartStrategy;
    }

    /**
     * @return the config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @return the marketDataClerk
     */
    public MarketDataClerk getMarketDataClerk() {
        return marketDataClerk;
    }

    /**
     * @return the cacheClerk
     */
    public MarketDataCacheClerk getCacheClerk() {
        return cacheClerk;
    }

    /**
     * @return the historicalDataClerk
     */
    public HistoricalDataClerk getHistoricalDataClerk() {
        return historicalDataClerk;
    }

    /**
     * @return the chartStrategy
     */
    public ChartStrategy getChartStrategy() {
        return chartStrategy;
    }

    /**
     * @param chartStrategy the chartStrategy to set
     */
    public void setChartStrategy(ChartStrategy chartStrategy) {
        this.chartStrategy = chartStrategy;
    }

    public TimeSeries buildTimeSeries() {
        // tip clerk get config
        Config config = getConfig();
        Instant start = Instant.now().minusSeconds(config.getLookback() * config.getBarSize().getSeconds());
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName(config.getDefaultSymbol()).build();
        
        String symbol = config.getDefaultSymbol();
        TickerType ticker = config.getTickerType();
        Duration barsize = config.getBarSize();
        
        List<Bar> bars = getHistoricalDataClerk().getHistoricalBars(symbol, ticker, start, Instant.now(), barsize);
        for (Bar b : bars) {
            BaseBar bar = new BaseBar(barsize, b.getTimestamp().atZone(ZoneOffset.UTC),
                    numOf(b.getOpen()), numOf(b.getHigh()), numOf(b.getLow()), numOf(b.getClose()), numOf(b.getVolume()), numOf(0));
            series.addBar(bar);
        }
        return series;
    }
    
    private Num numOf(Number num) {
        return PrecisionNum.valueOf(num);
    }
}