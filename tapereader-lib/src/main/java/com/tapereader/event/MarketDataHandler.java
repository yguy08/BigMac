package com.tapereader.event;

import com.tapereader.clerk.Clerk;
import com.tapereader.marketdata.MarketData;

public interface MarketDataHandler extends Clerk {

    void onMarketEvent(MarketData event);

}
