package com.tapereader.marketdata;

import java.util.List;

public interface MarketDataClerk {

    List<Tick> getCurrentTicks();

}
