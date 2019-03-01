package com.bigmac.marketdata.historical;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;

/**
 * The HistoricalDataClerk publishes historical prices of securities
 * 
 * @author wendre01
 *
 */
public interface HistoricalDataClerk {

    List<Bar> getHistoricalBars(String symbol, TickerType ticker, Instant startDate, Instant endDate, Duration duration);

    void updateBars(String symbol, TickerType ticker, Instant startDate, Instant endDate, Duration duration);

}
