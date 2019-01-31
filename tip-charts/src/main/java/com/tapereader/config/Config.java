package com.tapereader.config;

import java.time.Duration;

import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;

public class Config {
    
    private long lookback;
    
    private Duration barSize;
    
    private String defaultSymbol;
    
    private TickerType tickerType;
    
    private TipType defaultTip;
    
    private MarketType marketType;
    
    public Config() {
        
    }

    /**
     * @return the lookback
     */
    public long getLookback() {
        return lookback;
    }

    /**
     * @param lookback the lookback to set
     */
    public void setLookback(long lookback) {
        this.lookback = lookback;
    }

    /**
     * @return the barSize
     */
    public Duration getBarSize() {
        return barSize;
    }

    /**
     * @param barSize the barSize to set
     */
    public void setBarSize(Duration barSize) {
        this.barSize = barSize;
    }

    /**
     * @return the defaultSymbol
     */
    public String getDefaultSymbol() {
        return defaultSymbol;
    }

    /**
     * @param defaultSymbol the defaultSymbol to set
     */
    public void setDefaultSymbol(String defaultSymbol) {
        this.defaultSymbol = defaultSymbol;
    }

    /**
     * @return the tickerType
     */
    public TickerType getTickerType() {
        return tickerType;
    }

    /**
     * @param tickerType the tickerType to set
     */
    public void setTickerType(TickerType tickerType) {
        this.tickerType = tickerType;
    }

    /**
     * @return the defaultTip
     */
    public TipType getDefaultTip() {
        return defaultTip;
    }

    /**
     * @param defaultTip the defaultTip to set
     */
    public void setDefaultTip(TipType defaultTip) {
        this.defaultTip = defaultTip;
    }

    /**
     * @return the marketType
     */
    public MarketType getMarketType() {
        return marketType;
    }

    /**
     * @param marketType the marketType to set
     */
    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }
    
    
}
