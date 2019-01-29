package com.tapereader.reference;

import com.tapereader.enumeration.TickerType;

public class Security {

    private String symbol;
    
    private TickerType ticker;

    public Security() {

    }

    public Security(String symbol) {
        this.symbol = symbol;
    }
    
    public Security(String symbol, TickerType ticker) {
        setSymbol(symbol);
        setTickerType(ticker);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TickerType getTickerType() {
        return ticker;
    }

    public void setTickerType(TickerType ticker) {
        this.ticker = ticker;
    }

    @Override
    public String toString() {
        return symbol + ":" + ticker.toString();
    }

}
