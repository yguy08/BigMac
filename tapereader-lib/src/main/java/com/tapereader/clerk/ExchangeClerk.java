package com.tapereader.clerk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Order;
import com.tapereader.model.Security;

public interface ExchangeClerk extends Clerk {

    List<Tick> getCurrentTicks();

    Tick getLastTick(Security security);

    List<Security> getSecurities();

    Order sendOrder(Order order);

    boolean cancelOrder(Order order);

    boolean modifyOrder(Order order);

    List<Order> getOpenOrderDetails();

    List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate, Duration duration);

}
