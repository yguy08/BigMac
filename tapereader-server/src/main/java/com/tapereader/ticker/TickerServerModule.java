package com.tapereader.ticker;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.tapereader.adapter.BinanceExchangeClerk;
import com.tapereader.adapter.PoloniexExchangeClerk;
import com.tapereader.clerk.BinanceTicker;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.clerk.PoloniexTicker;
import com.tapereader.clerk.Ticker;
import com.tapereader.enumeration.TickerType;
import com.tapereader.event.DistributedMarketDataHandler;
import com.tapereader.event.MarketDataHandler;
import com.tapereader.wire.WireModule;

public class TickerServerModule extends AbstractModule {

    @Override
    protected void configure() {
        // app properties
        loadProperties("application.properties");

        // Messaging
        install(new WireModule());

        // Market Data
        bind(MarketDataHandler.class).to(DistributedMarketDataHandler.class);
        bind(Quotron.class).to(QuotronImpl.class);

        MapBinder<String, Ticker> mapBinder = MapBinder.newMapBinder(binder(), String.class, Ticker.class);

        // Find profile properties
        String profile = System.getenv("TR_PROFILE");
        if (profile == null) {
            throw new RuntimeException("Set TR_PROFILE evn variable");
        }
        String[] profiles = profile.split(",");
        for (String p : profiles) {
            if (p.trim().equalsIgnoreCase("BNC")) {
                mapBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceTicker.class);
            } else if (p.trim().equalsIgnoreCase("POLO")) {
                mapBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexTicker.class);
            }
        }
    }

    private void loadProperties(String propName) {
        Properties properties = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propName);
        try {
            properties.load(is);
            Names.bindProperties(binder(), properties);
            System.out.println("Loaded " + propName);
        } catch (Exception e) {
            System.out.println("Failed to load " + propName);
        }
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
