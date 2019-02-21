package com.tapereader.dao.bar;

import java.util.Collection;
import java.util.stream.Stream;

import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public interface BarDao {
    
    Stream<Bar> getAll() throws Exception;
    
    boolean add(Bar bar) throws Exception;
    
    boolean update(Bar bar) throws Exception;
    
    boolean add(Collection<Bar> bar) throws Exception;
    
    boolean delete(Bar bar) throws Exception;
    
    Stream<Bar> getAllBySymbolTickerAndDuration(String symbol, String ticker, long start, long end, long duration) throws Exception;
}
