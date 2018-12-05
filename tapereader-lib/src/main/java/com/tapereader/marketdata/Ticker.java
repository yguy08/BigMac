package com.tapereader.marketdata;

import com.tapereader.clerk.Clerk;
import com.tapereader.model.Security;

public interface Ticker extends Clerk {

    void startTicker();

    void stopTicker();

    void subscribe(String tipName, Security security);

}
