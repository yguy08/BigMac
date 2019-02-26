package com.tapereader.db.dao.tick;

import java.util.List;

import com.tapereader.db.dao.IDao;
import com.tapereader.marketdata.Tick;

public interface TickDao extends IDao<Tick> {
    
    void deleteAll() throws Exception;

    List<Tick> getAllByTicker(String ticker) throws Exception;

    Tick findBySymbolAndTicker(String symbol, String ticker) throws Exception;
}
