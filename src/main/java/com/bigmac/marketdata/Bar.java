package com.bigmac.marketdata;

import java.time.Duration;
import java.time.Instant;

import com.bigmac.domain.Symbol;

public class Bar extends MarketData {
    
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    // The bar's duration - daily/hourly/etc.
    private Duration duration;
    
    public Bar(Instant timestamp, Symbol symbol, double open, double high, double low,
            double close, int volume, Duration duration) {
        super(timestamp, symbol);
        this.open = open; 
        this.high = high; 
        this.low = low; 
        this.close = close;
        this.volume = volume;
        this.duration = duration;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    @Override
    public String toString() {
        return timestamp
                + " " + symbol
                + " " + duration
                + " " + open
                + " " + high
                + " " + low
                + " " + close
                + " " + volume;
    }
}
