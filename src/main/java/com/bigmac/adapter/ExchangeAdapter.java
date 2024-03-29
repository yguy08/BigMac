package com.bigmac.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.bigmac.marketdata.Bar;
import com.bigmac.marketdata.Tick;

public interface ExchangeAdapter {

    public Tick getCurrentTick(String symbol);

    public List<Tick> getCurrentTicks();

    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration);

}
