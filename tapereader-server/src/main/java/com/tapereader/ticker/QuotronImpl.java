package com.tapereader.ticker;

import java.util.Map;

import com.google.inject.Inject;
import com.tapereader.clerk.Ticker;

public class QuotronImpl implements Quotron {
    
    private Map<String, Ticker> tickerMap;
    
    @Inject
    private QuotronImpl(Map<String, Ticker> tickerMap) {
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
