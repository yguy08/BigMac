package com.tapereader.marketdata;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.db.dao.tick.TickDao;
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
    public Tick getCurrentTick(String symbol, TickerType ticker) {
        try {
            ExchangeAdapter adapter = adapterMap.get(ticker.toString());
            Tick tick = adapter.getCurrentTick(symbol);
            tickDao.save(tick);
            return tick;
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
            return null;
        }
    }

    @Override
    public List<Tick> getCurrentTicks(TickerType ticker) {
        try {
            ExchangeAdapter adapter = adapterMap.get(ticker.toString());
            List<Tick> ticks = adapter.getCurrentTicks();
            tickDao.save(ticks);
            return ticks;
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
            return null;
        }
    }

}
