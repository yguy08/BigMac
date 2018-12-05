package com.tapereader.marketdata;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.tapereader.enumeration.MessageType;

@Entity
@DiscriminatorValue(value="TICK")
@NamedQueries(value = {
        @NamedQuery(
                name = "Tick.findLatest",
                query = "SELECT t1 FROM MarketData t1 WHERE TYPE(t1) = Tick "
                        + "AND timestamp = (SELECT MAX(timestamp) FROM MarketData t2 "
                        + "WHERE t1.symbol = t2.symbol AND TYPE(t1) = TYPE(t2)) "
                + "GROUP BY symbol "
                + "ORDER BY symbol ASC"),
        @NamedQuery(
                name = "Tick.findBySecurity",
                query = "SELECT t FROM MarketData t "
                        + "WHERE t.symbol = :symbol "
                        + "AND TYPE(t) = TICK "
                        + "ORDER BY timestamp DESC")
})
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