package com.tapereader.marketdata.historical;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.tapereader.enumeration.TickerType;

public class NewspaperModule extends AbstractModule {
    
    @Override
    public void configure() {
        bind(Newspaper.class).to(NewspaperImpl.class);
        MapBinder<String, HistoricalDataClerk> mapBinder = MapBinder.newMapBinder(binder(), String.class, HistoricalDataClerk.class);
        mapBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceHistoricalDataClerk.class);
        mapBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexHistoricalDataClerk.class);
    }

}
