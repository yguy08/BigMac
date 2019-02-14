package com.tapereader.marketdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.dao.TickDao;
import com.tapereader.enumeration.TickerType;

public class MarketDataClerkImpl implements MarketDataClerk {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataClerkImpl.class);
    
    private final Map<String, ExchangeAdapter> adapterMap;
    
    private final TickDao tickDao;
    
    public MarketDataClerkImpl(Map<String, ExchangeAdapter> adapterMap, TickDao tickDao) {
        this.adapterMap = adapterMap;
        this.tickDao = tickDao;
    }

    @Override
    public List<Tick> getCurrentTicks() {
        try { 
            Stream<Tick> tickStream = tickDao.getAll();
            if (tickStream.findAny().isPresent()) {
                return tickDao.getAll().collect(Collectors.toList());
            }
            List<Tick> ticks = new ArrayList<>();
            for (Map.Entry<String, ExchangeAdapter> entry : adapterMap.entrySet()) {
                ticks.addAll(entry.getValue().getCurrentTicks());
            }
            tickDao.add(ticks);
            return ticks;
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
            return null;
        }
    }

    @Override
    public List<Tick> getCurrentTicks(TickerType ticker) {
        try {
            return tickDao.getAllByTicker(ticker.toString()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
            return null;
        }
    }

}
