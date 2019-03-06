package com.bigmac.marketdata;

import com.bigmac.domain.Symbol;
import com.bigmac.enumeration.TickerType;

public class Tick extends MarketData {

    private double last;

    private int volume;

    private double priceChangePercent;

    public Tick(long timestamp, String security, TickerType ticker, double last, int volume) {
        this(timestamp, security, ticker, last, volume, 0);
    }
    
    public Tick(long timestamp, String security, TickerType ticker, double last, int volume, double priceChangePercent) {
        super(timestamp, security, ticker);
        this.last = last;
        this.volume = volume;
        this.priceChangePercent = priceChangePercent;
    }
    
    public Tick(long timestamp, Symbol symbol, double last, int volume, double priceChangePercent) {
        this(timestamp, symbol.toString(), symbol.ticker, last, volume, priceChangePercent);
    }
    
    public Tick(long timestamp, String symbol, double last, int volume, double priceChangePercent) {
        this(timestamp, new Symbol(symbol), last, volume, priceChangePercent);
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getLast() {
        return last;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }
    
    /**
     * @return the priceChangePercent
     */
    public double getPriceChangePercent() {
        return priceChangePercent;
    }

    /**
     * @param priceChangePercent the priceChangePercent to set
     */
    public void setPriceChangePercent(double priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    @Override
    public String toString() {
        return timestamp 
                + " " + symbol
                + " " + last 
                + " " + volume;
    }
}