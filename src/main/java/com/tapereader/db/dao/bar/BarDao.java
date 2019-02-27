package com.tapereader.db.dao.bar;

import java.util.List;
import com.tapereader.db.dao.IDao;
import com.tapereader.marketdata.Bar;

public interface BarDao extends IDao<Bar> {

    List<Bar> getAllBySymbolTickerAndDuration(String symbol, String ticker, long start, long end, long duration) throws Exception;
    
    List<Bar> getAllBySymbolTicker(String symbol, String ticker, long start, long end) throws Exception;

    void deleteLastBarBySymbolTickerAndDuration(String symbol, String ticker, long duration) throws Exception;
}
