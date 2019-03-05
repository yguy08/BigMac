package com.bigmac.marketdata;

import com.bigmac.enumeration.TickerType;

public abstract class MarketData {

    // unix time in millis
    protected long timestamp;
    
    protected String symbol;
    
    protected TickerType ticker;

    public MarketData() {

    }

    public MarketData(long timestamp, String symbol, TickerType ticker) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.ticker = ticker;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String security) {
        this.symbol = security;
    }

    /**
     * @return the ticker
     */
    public TickerType getTicker() {
        return ticker;
    }

    /**
     * @param ticker the ticker to set
     */
    public void setTicker(TickerType ticker) {
        this.ticker = ticker;
    }

}
