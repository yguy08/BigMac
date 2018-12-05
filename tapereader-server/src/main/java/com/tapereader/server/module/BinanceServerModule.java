package com.tapereader.server.module;

import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.tapereader.annotation.Boot;
import com.tapereader.clerk.BinanceExchangeClerk;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.MarketDataClerkImpl;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.BinanceTicker;
import com.tapereader.marketdata.Quotron;
import com.tapereader.marketdata.QuotronImpl;
import com.tapereader.marketdata.Ticker;

@Boot(moduleName = "binance")
public class BinanceServerModule extends AbstractModule {

    @Override
    protected void configure() {
        // load properties from system property tr.properties
        loadProperties();

        // Market Data
        MapBinder<String, Ticker> mapBinder = MapBinder.newMapBinder(binder(), String.class, Ticker.class);
        mapBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceTicker.class);
        bind(ExchangeClerk.class).to(BinanceExchangeClerk.class);
        bind(Ticker.class).to(BinanceTicker.class);
        bind(Quotron.class).to(QuotronImpl.class);
        MapBinder<String, ExchangeClerk> exchangeClerkBinder = MapBinder.newMapBinder(binder(), String.class, ExchangeClerk.class);
        exchangeClerkBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceExchangeClerk.class);
        bind(MarketDataClerk.class).to(MarketDataClerkImpl.class);
    }

    private void loadProperties() {
        Properties properties = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("binance.properties");
        try {
            properties.load(is);
            Names.bindProperties(binder(), properties);
            System.out.println("Loaded binance.properties from resources");
        } catch (Exception e) {
            System.out.println("Failed to Load binance.properties from resources");
        }
    }

}
