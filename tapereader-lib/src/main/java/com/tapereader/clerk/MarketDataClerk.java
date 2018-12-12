package com.tapereader.clerk;

import java.util.List;

import com.tapereader.marketdata.Tick;

public interface MarketDataClerk extends Clerk {

    List<Tick> getCurrentTicks();

}
