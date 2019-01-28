package com.tapereader.marketdata;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.tapereader.enumeration.MessageType;

@Entity
@DiscriminatorValue(value="TICK")
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
    
    public Tick set(Tick t) {
        setTimestamp(t.timestamp);
        setSymbol(t.symbol);
        setLast(t.last);
        setVolume(t.volume);
        return this;
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
        return MessageType.TICK 
                + " " + timestamp 
                + " " + symbol
                + " " + last 
                + " " + volume;
    }
}