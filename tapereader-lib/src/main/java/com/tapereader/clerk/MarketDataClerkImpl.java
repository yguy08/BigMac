package com.tapereader.clerk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.inject.Inject;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.marketdata.Tick;

public class MarketDataClerkImpl implements MarketDataClerk {
    
    private Map<String, ExchangeAdapter> exchangeMap;
    
    @Inject
    private MarketDataClerkImpl(Map<String, ExchangeAdapter> exchangeMap) {
        this.exchangeMap = exchangeMap;
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
    }

    @Override
    public void terminate() {
        
    }

}
