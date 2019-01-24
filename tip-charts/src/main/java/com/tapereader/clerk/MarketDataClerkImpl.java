package com.tapereader.clerk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.marketdata.Tick;

@Singleton
public class MarketDataClerkImpl implements MarketDataClerk {
    
    private Map<String, ExchangeAdapter> exchangeMap;
    
    private Map<String, Ticker> tickerMap;
    
    @Inject
    private MarketDataClerkImpl(Map<String, ExchangeAdapter> exchangeMap, Map<String, Ticker> tickerMap) {
        this.exchangeMap = exchangeMap;
        this.tickerMap = tickerMap;
    }

    @Override
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = new ArrayList<>();
        for (Map.Entry<String, ExchangeAdapter> entry : exchangeMap.entrySet()) {
            ticks.addAll(entry.getValue().getCurrentTicks());
        }
        return ticks;
    }

    @Override
    public void init() {
        exchangeMap.values().stream().forEach(t -> t.init());
        tickerMap.values().stream().forEach(t -> t.init());
    }

    @Override
    public void terminate() {
        tickerMap.values().stream().forEach(t -> t.terminate());
    }

    @Override
    public void startStreaming() {
        tickerMap.values().stream().forEach(t -> t.startTicker());
    }

}
