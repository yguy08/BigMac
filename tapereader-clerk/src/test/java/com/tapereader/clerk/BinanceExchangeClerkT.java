package com.tapereader.clerk;

import com.tapereader.marketdata.Tick;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Security;

public class BinanceExchangeClerkT {

    public static void main(String[] args) {
        ExchangeClerk clerk = new BinanceExchangeClerk();
        clerk.init();
        Security security = new Security("BTC/USDT");
        security.setBucketShop(new BucketShop("BINANCE"));
        Tick tick = clerk.getLastTick(security);
        System.out.println(tick.toString());
    }

}
