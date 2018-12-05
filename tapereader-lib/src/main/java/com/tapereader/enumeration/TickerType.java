package com.tapereader.enumeration;


public enum TickerType {
    
    POLONIEX(1L),
    BINANCE(2L),
    CMC(3L),
    GDAX(4L),
    KUCOIN(5L);
    
    private long id;
    
    TickerType(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

}
