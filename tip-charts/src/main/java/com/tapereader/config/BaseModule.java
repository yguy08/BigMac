package com.tapereader.config;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Named;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.tapereader.adapter.BinanceExchangeAdapter;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.PoloniexExchangeAdapter;
import com.tapereader.clerk.BinanceTicker;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.clerk.JPAClerkImpl;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.MarketDataClerkImpl;
import com.tapereader.clerk.HistoricalDataClerk;
import com.tapereader.clerk.HistoricalDataClerkImpl;
import com.tapereader.clerk.PoloniexTicker;
import com.tapereader.clerk.MarketDataStreamingClerk;
import com.tapereader.clerk.MarketDataStreamingClerkImpl;
import com.tapereader.clerk.Ticker;
import com.tapereader.dao.LookupClerk;
import com.tapereader.dao.LookupClerkImpl;
import com.tapereader.dao.RecordClerk;
import com.tapereader.dao.RecordClerkImpl;
import com.tapereader.enumeration.TickerType;
import com.tapereader.event.DistributedMarketDataHandler;
import com.tapereader.event.MarketDataHandler;
import com.tapereader.wire.ActiveMQBroker;
import com.tapereader.wire.DefaultReceiver;
import com.tapereader.wire.DefaultTransmitter;
import com.tapereader.wire.MarketDataMessageProtocol;
import com.tapereader.wire.MessageProtocol;
import com.tapereader.wire.Receiver;
import com.tapereader.wire.Transmitter;

public class BaseModule extends AbstractModule {
    
    private final EventBus eventBus = new EventBus();
    
    private final String propFileName;
    
    public BaseModule(String propFileName) {
        this.propFileName = propFileName;
    }

    @Override
    protected void configure() {
        // Properties file
        loadProperties(propFileName);
        
        // JPA - Clerk Module
        bind(RecordClerk.class).to(RecordClerkImpl.class);
        bind(LookupClerk.class).to(LookupClerkImpl.class);
        
        // Historical Data Clerk
        bind(HistoricalDataClerk.class).to(HistoricalDataClerkImpl.class);
        
        // Wire - JMS
        bind(Receiver.class).to(DefaultReceiver.class);
        bind(Transmitter.class).to(DefaultTransmitter.class);
        bind(MessageProtocol.class).to(MarketDataMessageProtocol.class);
        bind(EventBus.class).toInstance(eventBus);
        
        // Market Data
        bind(MarketDataHandler.class).to(DistributedMarketDataHandler.class);
        bind(MarketDataStreamingClerk.class).to(MarketDataStreamingClerkImpl.class);
        bind(MarketDataClerk.class).to(MarketDataClerkImpl.class);

        MapBinder<String, Ticker> mapBinder = MapBinder.newMapBinder(binder(), String.class, Ticker.class);
        MapBinder<String, ExchangeAdapter> exchangeBinder 
        = MapBinder.newMapBinder(binder(), String.class, ExchangeAdapter.class);

        // Find profile properties
        String profile = System.getenv("TR_PROFILE");
        if (profile == null) {
            throw new RuntimeException("Set TR_PROFILE evn variable");
        }
        String[] profiles = profile.split(",");
        for (String p : profiles) {
            if (p.trim().equalsIgnoreCase("BNC")) {
                mapBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceTicker.class);
                exchangeBinder.addBinding(TickerType.BINANCE.toString()).to(BinanceExchangeAdapter.class);
            } else if (p.trim().equalsIgnoreCase("POLO")) {
                mapBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexTicker.class);
                exchangeBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexExchangeAdapter.class);
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
    
    @Provides
    public JPAClerk provideJPAClerk() {
        return JPAClerkImpl.INSTANCE;
    }
    
    @Provides
    public ActiveMQBroker activeMQBroker(@Named("mqbrokerurl") String brokerUrl) {
        return new ActiveMQBroker(brokerUrl);
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
