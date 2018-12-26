package com.tapereader.adapter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapereader.clerk.Clerk;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Security;

public interface ExchangeAdapter extends Clerk {

    List<Tick> getCurrentTicks();

    List<Bar> getHistoricalBars(Security security, Instant startDate, Instant endDate, Duration duration);

}
