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
        
        LOGGER.debug("Checking cache for bars.");
        List<Bar> bars = getCacheClerk().getHistoricalBars(symbol, ticker,
                start, Instant.now(), barsize);
        if (bars == null || bars.isEmpty()) {
            LOGGER.debug("No bars cached for {} from {} with bar size of {}. Getting new bars from exchange.", symbol, start, barsize);
            bars = getHistoricalDataClerk().getHistoricalBars(symbol, ticker, start, Instant.now(), barsize);
            LOGGER.debug("Got new bars from {} to {}", start, Instant.now());
        }
        Instant firstStart = Instant.ofEpochMilli(bars.get(0).getTimestamp());
        Instant end = Instant.ofEpochMilli(bars.get(bars.size() - 1).getTimestamp());
        boolean refreshCache = false;
        if (bars.size() < config.getLookback()) {
            LOGGER.debug("Found cached bars for {} from {} to {} with bar size of {}. "
                    + "But count {} does not match look back period of {}. Getting older bars.", 
                    symbol, firstStart, end, barsize, bars.size(), config.getLookback());
            getHistoricalDataClerk().updateBars(symbol, ticker, start, firstStart, barsize);
            refreshCache = true;
        }
        if (isOutOfDate(end, barsize)) {
            LOGGER.debug("Cached bars found but last bar is out of date...Getting new bars from exchange.");
            getHistoricalDataClerk().updateBars(symbol, ticker, end, Instant.now(), barsize);
            refreshCache = true;
        }
        if (refreshCache) {
            LOGGER.debug("Refreshing cache because updates were made.");
            // now get from updated cache
            bars = getCacheClerk().getHistoricalBars(symbol, ticker, start, Instant.now(), barsize);
        }
        LOGGER.debug("Bars up to Date! Symbol: {} Count: {} Start: {} End: {}",
                symbol, bars.size(), bars.get(0).getTimestamp(), bars.get(bars.size() - 1).getTimestamp());
        Bar lastBar = bars.get(bars.size() - 1);
        LOGGER.debug("Last bar to be updated {} ", lastBar);
        Tick tick = getCacheClerk().getCurrentTick(symbol, ticker);
        LOGGER.debug("Updating last price from latest tick: {} ", tick);
        if (tick != null) {
            double last = tick.getLast();
            lastBar.setClose(tick.getLast());
            if (lastBar.getLow() > tick.getLast()) {
                lastBar.setLow(last);
            } else if (lastBar.getHigh() < tick.getLast()) {
                lastBar.setHigh(last);
            }
        }
        for (Bar b : bars) {
            BaseBar bar = new BaseBar(barsize, Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    numOf(b.getOpen()), numOf(b.getHigh()), numOf(b.getLow()), numOf(b.getClose()), numOf(b.getVolume()), numOf(0));
            series.addBar(bar);
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
    
    private Num numOf(Number num) {
        return PrecisionNum.valueOf(num);
    }
}