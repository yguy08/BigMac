package com.tapereader.marketdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.tapereader.adapter.ExchangeAdapter;

public class MarketDataClerkImpl implements MarketDataClerk {
    
    private final Map<String, ExchangeAdapter> adapterMap;
    
    public MarketDataClerkImpl(Map<String, ExchangeAdapter> adapterMap) {
        this.adapterMap = adapterMap;
    }

    @Override
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = new ArrayList<>();
        for (Map.Entry<String, ExchangeAdapter> entry : adapterMap.entrySet()) {
            ticks.addAll(entry.getValue().getCurrentTicks());
        }
        return ticks;
    }

}
