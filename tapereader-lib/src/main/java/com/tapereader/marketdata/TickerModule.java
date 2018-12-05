package com.tapereader.marketdata;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.tapereader.enumeration.TickerType;

public class TickerModule extends AbstractModule {
    
    @Override
    public void configure() {
        MapBinder<String, Ticker> mapBinder = MapBinder.newMapBinder(binder(), String.class, Ticker.class);
        mapBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceTicker.class);
        mapBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexTicker.class);
        bind(Quotron.class).to(QuotronImpl.class);
    }

}
