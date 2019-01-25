package com.tapereader.clerk;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;

public class HistoricalDataClerkImpl implements HistoricalDataClerk {
    
    private Map<String, ExchangeAdapter> clerkMap;
    
    @Inject
    public HistoricalDataClerkImpl(Map<String, ExchangeAdapter> clerkMap) {
        this.clerkMap = clerkMap;
    }

    @Override
    public List<Bar> getHistoricalBars(Security security, Instant startDate, Instant endDate,
            Duration duration) {
        List<Bar> bars = clerkMap.get(security.getBucketShop().getName()).getHistoricalBars(security, startDate, endDate, duration);
        return bars;
    }

    @Override
    public void init() {
        
    }

    @Override
    public void terminate() {
        
    }

}
