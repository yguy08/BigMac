package com.tapereader.clerk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Order;
import com.tapereader.model.Security;

public interface MarketDataClerk extends Clerk {

    void subscribe(Security security, TickerType tickerType);

    List<Tick> getCurrentTicks();

    Tick getLastTick(Security security);

    List<Security> getSecurities(TickerType tickerType);

    Order sendOrder(Order order);

    List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate, Duration duration);

}
