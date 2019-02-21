package com.tapereader.marketdata.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public interface MarketDataCacheClerk {

    Tick getCurrentTick(String symbol, TickerType ticker);

    List<Tick> getCurrentTicks(TickerType ticker);

    List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate, Duration duration);
}
