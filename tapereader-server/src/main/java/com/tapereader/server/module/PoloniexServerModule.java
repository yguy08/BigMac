package com.tapereader.server.module;

import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.tapereader.annotation.Boot;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.BinanceTicker;
import com.tapereader.marketdata.PoloniexTicker;
import com.tapereader.marketdata.Quotron;
import com.tapereader.marketdata.QuotronImpl;
import com.tapereader.marketdata.Ticker;

@Boot(moduleName = "poloniex")
public class PoloniexServerModule extends AbstractModule {

    @Override
    protected void configure() {
        // load properties from system property tr.properties
        loadProperties();
        
        MapBinder<String, Ticker> mapBinder = MapBinder.newMapBinder(binder(), String.class, Ticker.class);
        mapBinder.addBinding(TickerType.POLONIEX.toString()).to(PoloniexTicker.class);
        bind(Ticker.class).to(BinanceTicker.class);
        bind(Quotron.class).to(QuotronImpl.class);
    }

    private void loadProperties() {
        Properties properties = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("poloniex.properties");
        try {
            properties.load(is);
            Names.bindProperties(binder(), properties);
            System.out.println("Loaded poloniex.properties from resources");
        } catch (Exception e) {
            System.out.println("Failed to Load poloniex.properties from resources");
        }
    }

}
