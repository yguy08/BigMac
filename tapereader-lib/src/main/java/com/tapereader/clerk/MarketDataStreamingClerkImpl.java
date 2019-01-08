package com.tapereader.clerk;

import java.util.Map;

import com.google.inject.Inject;

public class MarketDataStreamingClerkImpl implements MarketDataStreamingClerk {
    
    private Map<String, Ticker> tickerMap;
    
    @Inject
    private MarketDataStreamingClerkImpl(Map<String, Ticker> tickerMap) {
        this.tickerMap = tickerMap;
    }

    @Override
    public void init() {
        tickerMap.values().stream().forEach(t -> t.init());
    }

    @Override
    public void terminate() {
        tickerMap.values().stream().forEach(t -> t.terminate());
    }

    @Override
    public void startRecording() {
        tickerMap.values().stream().forEach(t -> t.startTicker());
    }

}
