package com.tapereader.marketdata.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.dao.bar.BarDao;
import com.tapereader.dao.tick.TickDao;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketDataClerkImpl;
import com.tapereader.marketdata.Tick;

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
            return tickDao.findBySymbolAndTicker(symbol, ticker.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Tick> getCurrentTicks(TickerType ticker) {
        try {
            return tickDao.getAllByTicker(ticker.toString()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("ERROR:", e);
            return null;
        }
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate,
            Duration duration) {
        try {
            return barDao.getAllBySymbolTickerAndDuration(symbol, ticker.toString(), 
                    startDate.toEpochMilli(), endDate.toEpochMilli(), duration.toMillis()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("ERROR:", e);
            return null;
        }
    }

}
