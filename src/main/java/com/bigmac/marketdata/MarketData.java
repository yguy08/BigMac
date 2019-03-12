package com.bigmac.marketdata;

import java.time.Instant;

import com.bigmac.domain.Symbol;

public abstract class MarketData {

    // unix time in millis
    public final Instant timestamp;
    
    public final Symbol symbol;

    public MarketData(Instant timestamp, Symbol symbol) {
        this.timestamp = timestamp;
        this.symbol = symbol;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }

    public Symbol getSymbol() {
        return symbol;
    }

}
