package com.bigmac.marketdata.historical;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigmac.adapter.ExchangeAdapter;
import com.bigmac.dao.BarDao;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;

public class HistoricalDataClerkImpl implements HistoricalDataClerk {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalDataClerkImpl.class);
    
    private final Map<String, ExchangeAdapter> adapterMap;
    
    private final BarDao barDao;
    
    public HistoricalDataClerkImpl(Map<String, ExchangeAdapter> adapterMap, BarDao barDao) {
        this.adapterMap = adapterMap;
        this.barDao = barDao;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate,
            Duration duration) {
        List<Bar> bars = null;
        try {
            ExchangeAdapter adapter = adapterMap.get(ticker.toString());
            bars = adapter.getHistoricalBars(symbol, startDate, endDate, duration);
            barDao.save(bars);
            return bars;
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return bars;
    }

    @Override
    public void updateBars(String symbol, TickerType ticker, Instant startDate, Instant endDate, Duration duration) {
        List<Bar> bars = null;
        try {
            LOGGER.debug("Updating bars for {} from {} to {} with bar size of {}", symbol, startDate, endDate, duration);
            ExchangeAdapter adapter = adapterMap.get(ticker.toString());
            bars = adapter.getHistoricalBars(symbol, startDate, endDate, duration);
            Bar firstBar = barDao.findBySymbolTickerDurationAndTimestamp(symbol, ticker.toString(), duration.toMillis(), startDate.toEpochMilli());
            Bar lastBar = barDao.findBySymbolTickerDurationAndTimestamp(symbol, ticker.toString(), duration.toMillis(), endDate.toEpochMilli());
            if (firstBar != null) {
                barDao.delete(firstBar);
            }
            if (lastBar != null) {
                barDao.delete(lastBar);
            }
            barDao.save(bars);
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
    }

}
