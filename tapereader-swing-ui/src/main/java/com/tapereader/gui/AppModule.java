package com.tapereader.gui;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.tapereader.clerk.ClerkModule;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.MarketDataClerkImpl;
import com.tapereader.config.ConfigModule;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.historical.NewspaperModule;
import com.tapereader.tip.TapeReader;
import com.tapereader.adapter.BinanceExchangeAdapter;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.PoloniexExchangeAdapter;
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
        MapBinder<String, ExchangeAdapter> exchangeClerkBinder = MapBinder.newMapBinder(binder(), String.class, ExchangeAdapter.class);
        exchangeClerkBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceExchangeAdapter.class);
        exchangeClerkBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexExchangeAdapter.class);
        bind(MarketDataClerk.class).to(MarketDataClerkImpl.class);
    }
    
    @Named("bncExchangeClerk")
    @Provides
    public ExchangeAdapter bncExchange() {
        return new BinanceExchangeAdapter();
    }

    @Named("poloExchangeClerk")
    @Provides
    public ExchangeAdapter poloExchange() {
        return new PoloniexExchangeAdapter();
    }
}
