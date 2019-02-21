package com.tapereader.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public interface ExchangeAdapter {
    
    public boolean init();

    public Tick getCurrentTick(String symbol);

    public List<Tick> getCurrentTicks();

    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration);

}
