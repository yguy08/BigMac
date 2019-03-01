package com.bigmac.dao;

import java.util.List;

import com.bigmac.marketdata.Bar;

public interface BarDao extends IDao<Bar> {

    List<Bar> getAllBySymbolTickerAndDuration(String symbol, String ticker, long start, long end, long duration) throws Exception;

    void deleteLastBarBySymbolTickerAndDuration(String symbol, String ticker, long duration) throws Exception;
    
    Bar findBySymbolTickerDurationAndTimestamp(String symbol, String ticker, long duration, long timestamp) throws Exception;
}
