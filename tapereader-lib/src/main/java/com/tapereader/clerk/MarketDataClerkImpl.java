package com.tapereader.clerk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.inject.Inject;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Order;
import com.tapereader.model.Security;

public class MarketDataClerkImpl implements MarketDataClerk {
    
    private Map<String, ExchangeClerk> exchangeMap;
    
    @Inject
    private MarketDataClerkImpl(Map<String, ExchangeClerk> exchangeMap) {
        this.exchangeMap = exchangeMap;
    }

    @Override
    public void subscribe(Security security, TickerType tickerType) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = new ArrayList<>();
        for (Map.Entry<String, ExchangeClerk> entry : exchangeMap.entrySet()) {
            ticks.addAll(entry.getValue().getCurrentTicks());
        }
        return ticks;
    }

    @Override
    public Tick getLastTick(Security security) {
        return exchangeMap.get(security.getBucketShop().getName()).getLastTick(security);
    }

    @Override
    public List<Security> getSecurities(TickerType tickerType) {
        return exchangeMap.get(tickerType.toString()).getSecurities();
    }

    @Override
    public Order sendOrder(Order order) {
        return null;
    }

    @Override
    public List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate,
            Duration duration) {
        return null;
    }

    @Override
    public void init() {
        exchangeMap.values().stream().forEach(t -> t.init());
    }

    @Override
    public void terminate() {
        
    }

}
