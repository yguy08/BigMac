package com.tapereader.gui.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.chart.ChartManager;
import com.tapereader.chart.strategy.ChartStrategy;
import com.tapereader.config.Config;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.Tick;
import com.tapereader.marketdata.cache.MarketDataCacheClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerk;

public class TipClerk {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TipClerk.class);
    
    private final Config config;
    
    private final MarketDataClerk marketDataClerk;
    
    private final MarketDataCacheClerk cacheClerk;
    
    private final HistoricalDataClerk historicalDataClerk;
    
    private ChartManager chartManager;
    
    private ChartStrategy chartStrategy;
    
    public TipClerk(Config config, MarketDataClerk marketDataClerk, HistoricalDataClerk historicalDataClerk, 
            MarketDataCacheClerk cacheClerk, ChartManager chartManager, ChartStrategy chartStrategy) {
        this.config = config;
        this.marketDataClerk = marketDataClerk;
        this.cacheClerk = cacheClerk;
        this.historicalDataClerk = historicalDataClerk;
        this.chartManager = chartManager;
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
     * @return the chartManager
     */
    public ChartManager getChartManager() {
        return chartManager;
    }

    /**
     * @param chartManager the chartManager to set
     */
    public void setChartManager(ChartManager chartManager) {
        this.chartManager = chartManager;
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
        
        List<Bar> bars = getCacheClerk().getHistoricalBars(symbol, ticker,
                start, Instant.now(), barsize);
        if (bars == null || bars.isEmpty()) {
            LOGGER.info("No bars cached...Getting new bars from exchange.");
            bars = getHistoricalDataClerk().getHistoricalBars(symbol, ticker, start, Instant.now(), barsize);
        } else {
            Instant lastTS = Instant.ofEpochMilli(bars.get(bars.size() - 1).getTimestamp());
            if (isOutOfDate(lastTS, barsize)) {
                LOGGER.info("Cached bars found but out of date...Getting new bars from exchange.");
                getHistoricalDataClerk().updateBars(symbol, ticker, start, barsize);
                bars = getCacheClerk().getHistoricalBars(symbol, ticker, start, Instant.now(), barsize);
            } else {
                LOGGER.info("Using cached bars. Updating last price from latest tick.");
                Bar bar = bars.get(bars.size() - 1);
                Tick tick = getCacheClerk().getCurrentTick(symbol, ticker);
                if (tick != null) {
                    bar.setClose(tick.getLast());
                }
            }
        }
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        return series;
    }
    
    private boolean isOutOfDate(Instant lastTS, Duration barSize) {
        Instant outOfDateTS;
        Instant lastTrunc;
        if (barSize.toDays() > 0) {
            outOfDateTS = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
            lastTrunc = lastTS.truncatedTo(ChronoUnit.DAYS);
        } else if (barSize.toHours() > 0) {
            outOfDateTS = Instant.now().truncatedTo(ChronoUnit.HOURS).minus(1, ChronoUnit.HOURS);
            lastTrunc = lastTS.truncatedTo(ChronoUnit.HOURS);
        } else {
            outOfDateTS = Instant.now().truncatedTo(ChronoUnit.MINUTES).minus(1, ChronoUnit.MINUTES);
            lastTrunc = lastTS.truncatedTo(ChronoUnit.MINUTES);
        }
        if (lastTrunc.compareTo(outOfDateTS) < 1){
            return true;
        } else {
            return false;
        }
    }
}