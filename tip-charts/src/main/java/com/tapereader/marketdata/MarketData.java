package com.tapereader.marketdata;

public abstract class MarketData {

    // unix time in millis
    protected long timestamp;
    
    protected String symbol;

    public MarketData() {

    }

    public MarketData(long timestamp, String symbol) {
        this.timestamp = timestamp;
        this.symbol = symbol;
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

}
