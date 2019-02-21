package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.dao.bar.BarDao;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;

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
            adapterMap.get(ticker.toString()).init();
            bars = adapterMap.get(ticker.toString()).getHistoricalBars(symbol, startDate, endDate, duration);
            barDao.add(bars);
            return bars;
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return bars;
    }

}
