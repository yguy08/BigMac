package com.tapereader.tip;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.config.Config;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerk;

public class TipClerk {
    
    private final Config config;
    
    private final MarketDataClerk marketDataClerk;
    
    private final HistoricalDataClerk historicalDataClerk;
    
    private Tip tip;
    
    public TipClerk(Config config, MarketDataClerk marketDataClerk, HistoricalDataClerk historicalDataClerk, Tip tip) {
        this.config = config;
        this.marketDataClerk = marketDataClerk;
        this.historicalDataClerk = historicalDataClerk;
        this.tip = tip;
    }

    /**
     * @return the config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @return the tipName
     */
    public Tip getTip() {
        return tip;
    }

    /**
     * @param tipName the tipName to set
     */
    public void setTip(Tip tip) {
        this.tip = tip;
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