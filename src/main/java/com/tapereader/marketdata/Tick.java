package com.tapereader.marketdata;

public class Tick extends MarketData {

    private double last;
    
    private int volume;
    
    private double priceChangePercent;
    
    public Tick() {

    }

    public Tick(long timestamp, String security, double last, int volume) {
        this(timestamp, security, last, volume, 0);
    }
    
    public Tick(long timestamp, String security, double last, int volume, double priceChangePercent) {
        super(timestamp, security);
        this.last = last;
        this.volume = volume;
        this.priceChangePercent = priceChangePercent;
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