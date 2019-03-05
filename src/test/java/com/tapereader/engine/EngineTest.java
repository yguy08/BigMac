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
    
    private static Instant lastInstant = null;
    
    public EngineTest() {
        BarDao barDao = new BarDaoImpl(Application.createDataSource());
        try {
            List<Bar> bars = barDao.getAllBySymbolTicker("BTC/USDT", TickerType.BINANCE.toString(), 
                    Instant.now().minus(100, ChronoUnit.DAYS).toEpochMilli(), Instant.now().toEpochMilli());
            
            Engine engine = new EngineConfig().engine();
            engine.loadStatements("BarToBar", this);
            System.out.println("Total bar count: " + bars.size());
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
        Instant nextInstant = Instant.ofEpochMilli(marketData.getTimestamp());
        if (lastInstant != null) {
            if (nextInstant.getEpochSecond() - lastInstant.getEpochSecond() < 86400) {
                System.out.println(lastInstant);
                System.out.println(nextInstant);
            }
        }
        System.out.println(marketData);
        System.out.println(count++);
        lastInstant = nextInstant;
    }

}
