package com.bigmac.marketdata.cache;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigmac.dao.BarDao;
import com.bigmac.dao.TickDao;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.MarketDataClerkImpl;
import com.bigmac.marketdata.Tick;

public class MarketDataCacheClerkImpl implements MarketDataCacheClerk {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataClerkImpl.class);
    
    private final TickDao tickDao;
    
    private final BarDao barDao;
    
    public MarketDataCacheClerkImpl(TickDao tickDao, BarDao barDao) {
        this.tickDao = tickDao;
        this.barDao = barDao;
    }

    @Override
    public Tick getCurrentTick(String symbol, TickerType ticker) {
        try {
            return tickDao.findBySymbolAndTicker(symbol, ticker.getCode());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Tick> getCurrentTicks(TickerType ticker) {
        try {
            return tickDao.getAllByTicker(ticker.getCode());
        } catch (Exception e) {
            LOGGER.error("ERROR:", e);
            return null;
        }
    }

    @Override
    public void clearTickCache() {
        try {
            tickDao.deleteAll();
        } catch (Exception e) {
            LOGGER.error("ERROR:", e);
        }
    }

}
