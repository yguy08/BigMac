package com.bigmac.marketdata.cache;

import java.util.List;

import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Tick;

public interface MarketDataCacheClerk {

    Tick getCurrentTick(String symbol, TickerType ticker);

    List<Tick> getCurrentTicks(TickerType ticker);

    void clearTickCache();

}
