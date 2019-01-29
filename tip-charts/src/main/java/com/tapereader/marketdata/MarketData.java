package com.tapereader.marketdata;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(
name="MARKET_DATA", 
indexes = { @Index(name = "IDX_TICKIDX1", columnList = "symbol") })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE", discriminatorType=DiscriminatorType.STRING)
public abstract class MarketData {

    @Id
    @GeneratedValue
    private Long id;

    // unix time in millis
    protected long timestamp;
    
    protected String symbol;

    public MarketData() {

    }

    public MarketData(long timestamp, String symbol) {
        this.timestamp = timestamp;
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
