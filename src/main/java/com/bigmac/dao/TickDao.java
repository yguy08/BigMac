package com.bigmac.dao;

import java.util.List;

import com.bigmac.marketdata.Tick;

public interface TickDao extends IDao<Tick> {
    
    void deleteAll() throws Exception;

    List<Tick> getAllByTicker(String ticker) throws Exception;

    Tick findBySymbolAndTicker(String symbol, String ticker) throws Exception;
}
