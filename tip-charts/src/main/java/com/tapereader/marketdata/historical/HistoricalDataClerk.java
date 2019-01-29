package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;

/**
 * The HistoricalDataClerk publishes historical prices of securities
 * 
 * @author wendre01
 *
 */
public interface HistoricalDataClerk {

    List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate, Duration duration);

}
