package com.bigmac.marketdata.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;
import com.bigmac.marketdata.Tick;

public interface MarketDataCacheClerk {

    Tick getCurrentTick(String symbol, TickerType ticker);

    List<Tick> getCurrentTicks(TickerType ticker);

    List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate, Duration duration);
    
    void clearTickCache();
    
    void updateLastBar(Bar bar);
}
