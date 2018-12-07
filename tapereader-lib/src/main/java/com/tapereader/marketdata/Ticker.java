package com.tapereader.marketdata;

import com.tapereader.clerk.Clerk;

public interface Ticker extends Clerk {

    void startTicker();

    void stopTicker();

}
