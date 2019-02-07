package com.tapereader.chart;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.chart.strategy.ChartStrategy;
import com.tapereader.config.Config;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerk;

public class TipClerk {
    
    private final Config config;
    
    private final MarketDataClerk marketDataClerk;
    
    private final HistoricalDataClerk historicalDataClerk;
    
    private ChartManager chartManager;
    
    private ChartStrategy chartStrategy;
    
    public TipClerk(Config config, MarketDataClerk marketDataClerk, HistoricalDataClerk historicalDataClerk, 
            ChartManager chartManager, ChartStrategy chartStrategy) {
        this.config = config;
        this.marketDataClerk = marketDataClerk;
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
        List<Bar> bars = getHistoricalDataClerk()
                .getHistoricalBars(config.getDefaultSymbol(), config.getTickerType(), start, Instant.now(), config.getBarSize());
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        return series;
    }
}