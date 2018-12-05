package com.tapereader.event;

import com.google.inject.Singleton;
import com.tapereader.marketdata.MarketData;
import com.tapereader.wire.DefaultTransmitter;

@Singleton
public class DistributedMarketDataHandler extends DefaultTransmitter implements MarketDataHandler {

    @Override
    public void onMarketEvent(MarketData event) {
        try {
            transmit(event);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
