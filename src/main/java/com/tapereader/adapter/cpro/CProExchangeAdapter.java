package com.tapereader.adapter.cpro;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public class CProExchangeAdapter implements ExchangeAdapter {

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void terminate() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Tick> getCurrentTicks() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration) {
        // TODO Auto-generated method stub
        return null;
    }

}
