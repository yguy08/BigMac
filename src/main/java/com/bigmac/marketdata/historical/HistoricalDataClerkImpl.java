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
    
    public HistoricalDataClerkImpl(Map<String, ExchangeAdapter> adapterMap) {
        this.adapterMap = adapterMap;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate,
            Duration duration) {
        List<Bar> bars = null;
        try {
            ExchangeAdapter adapter = adapterMap.get(ticker.toString());
            bars = adapter.getHistoricalBars(symbol, startDate, endDate, duration);
            return bars;
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }
        return bars;
    }

}
