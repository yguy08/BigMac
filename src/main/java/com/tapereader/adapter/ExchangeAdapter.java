package com.tapereader.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapereader.adapter.bnc.BinanceExchangeAdapter;
import com.tapereader.adapter.polo.PoloniexExchangeAdapter;
import com.tapereader.clerk.Clerk;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public interface ExchangeAdapter extends Clerk {

    public static ExchangeAdapter makeFactory(TickerType type) {
        switch (type) {
        case POLONIEX:
            return new PoloniexExchangeAdapter();
        case BINANCE:
            return new BinanceExchangeAdapter();
        default:
            throw new IllegalArgumentException("Exchange Adapter for " + type + " not supported.");
        }
    }

    public List<Tick> getCurrentTicks();

    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration);

}
