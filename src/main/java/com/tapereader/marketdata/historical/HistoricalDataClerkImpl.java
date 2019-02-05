package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;

public class HistoricalDataClerkImpl implements HistoricalDataClerk {
    
    private final Map<String, ExchangeAdapter> adapterMap;
    
    public HistoricalDataClerkImpl(Map<String, ExchangeAdapter> adapterMap) {
        this.adapterMap = adapterMap;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate,
            Duration duration) {
        List<Bar> bars = adapterMap.get(ticker.toString()).getHistoricalBars(symbol, startDate, endDate, duration);
        return bars;
    }

}
