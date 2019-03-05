package com.bigmac.config;

import java.time.Duration;

import com.bigmac.enumeration.MarketType;
import com.bigmac.enumeration.TickerType;
import com.bigmac.enumeration.TipType;

public class Config {
    
    private long lookback;
    
    private Duration barSize;
    
    private String defaultSymbol;
    
    private TickerType tickerType;
    
    private TipType defaultTip;
    
    private MarketType marketType;
    
    private boolean filterDelisted;
    
    private long outOfDateSeconds;
    
    private static boolean useBarCache = true;
    
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

    /**
     * @return the filterDelisted
     */
    public boolean isFilterDelisted() {
        return filterDelisted;
    }

    /**
     * @param filterDelisted the filterDelisted to set
     */
    public void setFilterDelisted(boolean filterDelisted) {
        this.filterDelisted = filterDelisted;
    }

    /**
     * @return the outOfDateSeconds
     */
    public long getOutOfDateSeconds() {
        return outOfDateSeconds;
    }

    /**
     * @param outOfDateSeconds the outOfDateSeconds to set
     */
    public void setOutOfDateSeconds(long outOfDateSeconds) {
        this.outOfDateSeconds = outOfDateSeconds;
    }

    /**
     * @return the useBarCache
     */
    public static boolean isUseBarCache() {
        return useBarCache;
    }

    /**
     * @param useBarCache the useBarCache to set
     */
    public static void setUseBarCache(boolean useBarCache) {
        Config.useBarCache = useBarCache;
    }
    
    
}
