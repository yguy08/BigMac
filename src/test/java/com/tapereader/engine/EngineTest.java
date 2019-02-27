package com.tapereader.engine;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.tapereader.Application;
import com.tapereader.db.dao.bar.BarDao;
import com.tapereader.db.dao.bar.BarDaoImpl;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketData;

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
