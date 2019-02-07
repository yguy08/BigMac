package com.tapereader.adapter.bnc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.Test;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public class BinanceAdapterTest {
    
    @Test
    public void binanceAdapterTest() {
        ExchangeAdapter bncAdapter = new BinanceExchangeAdapter();
        bncAdapter.init();
        
        List<Tick> ticks = bncAdapter.getCurrentTicks();
        assertFalse(ticks.isEmpty());
        
        List<Bar> bars = bncAdapter.getHistoricalBars("BTC/USDT", Instant.now().minus(5, ChronoUnit.DAYS), Instant.now(), Duration.ofDays(1));
        assertFalse(bars.isEmpty());
        assertTrue(bars.size() == 5);
    }

}
