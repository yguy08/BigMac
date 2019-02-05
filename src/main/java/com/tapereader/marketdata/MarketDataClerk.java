package com.tapereader.marketdata;

import java.util.List;

import com.tapereader.enumeration.TickerType;

public interface MarketDataClerk {

    List<Tick> getCurrentTicks();
    
    List<Tick> getCurrentTicks(TickerType ticker);

}
