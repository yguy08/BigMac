package com.tapereader.gui;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.tapereader.clerk.ClerkModule;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.MarketDataClerkImpl;
import com.tapereader.config.ConfigModule;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.historical.NewspaperModule;
import com.tapereader.tip.TapeReader;
import com.tapereader.adapter.BinanceExchangeClerk;
import com.tapereader.adapter.PoloniexExchangeClerk;
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
        exchangeClerkBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexExchangeClerk.class);
        bind(MarketDataClerk.class).to(MarketDataClerkImpl.class);
    }
    
    @Named("bncExchangeClerk")
    @Provides
    public ExchangeClerk bncExchange() {
        return new BinanceExchangeClerk();
    }

    @Named("poloExchangeClerk")
    @Provides
    public ExchangeClerk poloExchange() {
        return new PoloniexExchangeClerk();
    }
}
