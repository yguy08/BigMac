package com.bigmac.marketdata;

import java.time.Instant;

import com.bigmac.domain.Symbol;
import com.bigmac.enumeration.TickerType;

public abstract class MarketData {

    // unix time in millis
    public final Instant timestamp;
    
    public final Symbol symbol;

    public MarketData(Instant timestamp, Symbol symbol) {
        this.timestamp = timestamp;
        this.symbol = symbol;
    }

    public MarketData(long millis, String symbol, TickerType ticker) {
        this(Instant.ofEpochMilli(millis), new Symbol(symbol, ticker));
    }
    
    public MarketData(long millis, String symbol, String ticker) {
        this(Instant.ofEpochMilli(millis), new Symbol(symbol, TickerType.enumOf(ticker)));
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }

    public Symbol getSymbol() {
        return symbol;
    }

}
