package com.tapereader.marketdata;

public class Tick extends MarketData {

    private double last;
    
    private int volume;
    
    public Tick() {

    }

    public Tick(long timestamp, String security, double last, int volume) {
        super(timestamp, security);
        this.last = last;
        this.volume = volume;
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
    
    @Override
    public String toString() {
        return timestamp 
                + " " + symbol
                + " " + last 
                + " " + volume;
    }
}