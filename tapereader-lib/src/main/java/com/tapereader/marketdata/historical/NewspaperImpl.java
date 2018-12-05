package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;

public class NewspaperImpl implements Newspaper {
    
    private Map<String, HistoricalDataClerk> clerkMap;
    
    @Inject
    public NewspaperImpl(Map<String, HistoricalDataClerk> clerkMap) {
        this.clerkMap = clerkMap;
    }

    @Override
    public List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate,
            Duration duration) {
        List<Bar> bars = clerkMap.get(security.getBucketShop().getName()).getHistoricalBars(security, startDate, endDate, duration);
        return bars;
    }

    @Override
    public void init() {
        clerkMap.entrySet().stream().forEach(h -> h.getValue().init());
    }

    @Override
    public void terminate() {
        
    }

}