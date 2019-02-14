package com.tapereader.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.tapereader.marketdata.Tick;

public interface TickDao {
    
    Stream<Tick> getAll() throws Exception;
    
    boolean add(Tick tick) throws Exception;
    
    boolean update(Tick tick) throws Exception;
    
    boolean add(Collection<Tick> tick) throws Exception;
    
    boolean delete(Tick tick) throws Exception;
    
    Stream<Tick> getAllByTicker(String ticker) throws Exception;
}
