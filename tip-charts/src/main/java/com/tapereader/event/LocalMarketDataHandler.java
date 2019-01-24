package com.tapereader.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.tapereader.marketdata.MarketData;

public class LocalMarketDataHandler implements MarketDataHandler {

    private final EventBus eventBus;

    @Inject
    public LocalMarketDataHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onMarketEvent(MarketData event) {
        eventBus.post(event);
    }

    @Override
    public void init() {
        
    }

    @Override
    public void terminate() {
        
    }

}
