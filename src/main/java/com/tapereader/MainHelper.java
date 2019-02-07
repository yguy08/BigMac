package com.tapereader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.reflect.ClassPath;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.bnc.BinanceExchangeAdapter;
import com.tapereader.adapter.polo.PoloniexExchangeAdapter;
import com.tapereader.chart.ChartManager;
import com.tapereader.chart.TipClerk;
import com.tapereader.chart.strategy.buyhigh.BuyHigh;
import com.tapereader.config.Config;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.gui.TRGuiMain;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.MarketDataClerkImpl;
import com.tapereader.marketdata.historical.HistoricalDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerkImpl;

public class MainHelper {
    
    private final String[] args;
    
    private final String propertiesFileName;
    
    public MainHelper(String[] args, String propertiesFileName) {
        this.args = args;
        this.propertiesFileName = propertiesFileName;
    }
    
    public void run() {
        /* Load Properties File */
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(propertiesFileName);
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties file.");
        }
        
        ExchangeAdapter bncAdapter = new BinanceExchangeAdapter();
        bncAdapter.init();
        
        ExchangeAdapter poloAdapter = new PoloniexExchangeAdapter();
        poloAdapter.init();
        
        Map<String, ExchangeAdapter> adapterMap = new HashMap<>();
        adapterMap.put(TickerType.BINANCE.toString(), bncAdapter);
        adapterMap.put(TickerType.POLONIEX.toString(), poloAdapter);
        
        MarketDataClerk marketDataClerk = new MarketDataClerkImpl(adapterMap);
        HistoricalDataClerk historicalDataClerk = new HistoricalDataClerkImpl(adapterMap);
        
        Config config = new Config();
        config.setBarSize(Duration.ofDays(1));
        config.setDefaultSymbol("BTC/USDT");
        config.setDefaultTip(TipType.BUY_HIGH);
        config.setLookback(100);
        config.setTickerType(TickerType.BINANCE);
        config.setMarketType(MarketType.BTC);
        
        TipClerk tipClerk = new TipClerk(config, marketDataClerk, historicalDataClerk, new ChartManager(), new BuyHigh());
        
        /* Parse properties and set value annotation */
        try {
            ClassPath classpath = ClassPath.from(MainHelper.class.getClassLoader());
            for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive("com.tapereader")) {
                Class<?> clazz = classInfo.load();
                System.out.println("Class: " + clazz.getSimpleName());
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getDeclaredAnnotation(Property.class) != null) {
                        Property property = field.getDeclaredAnnotation(Property.class);
                        Object o = properties.getProperty(property.value());
                        System.out.println(o);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        TRGuiMain app = new TRGuiMain(tipClerk);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.createAndShowGui();
            }
        });
    }

}
