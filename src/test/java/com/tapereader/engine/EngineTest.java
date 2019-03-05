package com.tapereader.engine;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.bigmac.Application;
import com.bigmac.dao.BarDao;
import com.bigmac.dao.BarDaoImpl;
import com.bigmac.engine.Engine;
import com.bigmac.engine.EngineConfig;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;
import com.bigmac.marketdata.MarketData;

public class EngineTest {
    
    private static int count = 1;
    
    public EngineTest() {
        BarDao barDao = new BarDaoImpl(Application.createDataSource());
        try {
            List<Bar> bars = barDao.getAllBySymbolTicker("BTC/USDT", TickerType.BINANCE.toString(), 
                    Instant.now().minus(100, ChronoUnit.DAYS).toEpochMilli(), Instant.now().toEpochMilli());
            
            Engine engine = new EngineConfig().engine();
            engine.loadStatements("BarToBar", this);
            for (Bar bar : bars) {
                engine.sendEvent(bar);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static void main(String[] args) {
        new EngineTest();
    }
    
    public void sendMarketDataEvent(MarketData marketData) {
        System.out.println(marketData);
        System.out.println(count++);
    }

}
