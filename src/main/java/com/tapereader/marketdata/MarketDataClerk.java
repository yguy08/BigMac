package com.tapereader.marketdata;

import java.util.List;

import com.tapereader.enumeration.TickerType;

public interface MarketDataClerk {

    Tick getCurrentTick(String symbol, TickerType ticker);

    List<Tick> getCurrentTicks(TickerType ticker);

}
