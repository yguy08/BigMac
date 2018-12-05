package com.tapereader.gui;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.tapereader.clerk.BinanceExchangeClerk;
import com.tapereader.clerk.ClerkModule;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.MarketDataClerkImpl;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.historical.NewspaperModule;
import com.tapereader.tip.TapeReader;
import com.tapereader.boot.ConfigModule;
import com.tapereader.bus.TrRoleLogic;
import com.tapereader.wire.WireModule;

public class AppModule extends AbstractModule {
    @Override 
    public void configure() {
        install(new ClerkModule());
        install(new ConfigModule());
        install(new NewspaperModule());
        install(new WireModule());
        bind(TapeReader.class).to(TrRoleLogic.class);
        bind(TapeReaderGuiMain.class);
        MapBinder<String, ExchangeClerk> exchangeClerkBinder = MapBinder.newMapBinder(binder(), String.class, ExchangeClerk.class);
        exchangeClerkBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceExchangeClerk.class);
        bind(MarketDataClerk.class).to(MarketDataClerkImpl.class);
    }
}
