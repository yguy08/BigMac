package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.tapereader.clerk.Clerk;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;

/**
 * The Newspaper publishes historical prices of securities
 * 
 * @author wendre01
 *
 */
public interface Newspaper extends Clerk {

    List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate, Duration duration);

}
