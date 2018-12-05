package com.tapereader.clerk;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Order;
import com.tapereader.model.Security;
import com.tapereader.util.TradingUtils;
import com.tapereader.util.UniqueTimeMicros;

public class BinanceExchangeClerk implements ExchangeClerk {
    
    private Exchange exchange;
    
    private BinanceMarketDataService marketDataService;
    
    @Inject(optional = true)
    @Named("binance.throttle.seconds")
    private String throttle = "3600";
    
    @Inject(optional = true)
    @Named("binance.volume.filter")
    private String minVol = "50";

    @Override
    public void init() {
        exchange = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
        marketDataService = (BinanceMarketDataService) exchange.getMarketDataService();
    }

    @Override
    public void terminate() {
        
    }

    @Override
    public List<Tick> getCurrentTicks() {
        try {
            return ((BinanceMarketDataServiceRaw) marketDataService)
            .ticker24h()
            .stream()
            .filter(t -> t.getQuoteVolume().intValue() > 50)
            .map(t -> binance24hourToTick.apply(t.getSymbol(), t))
            .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Tick getLastTick(Security security) {
        try {
            org.knowm.xchange.dto.marketdata.Ticker ticker = marketDataService.getTicker(new CurrencyPair(security.getSymbol()));
            return tickerToTick.apply(security.getSymbol(), ticker);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Security> getSecurities() {
        return exchange
                .getExchangeSymbols()
                .parallelStream()
                .map(p -> new Security(p.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public Order sendOrder(Order order) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private static BiFunction<String, org.knowm.xchange.dto.marketdata.Ticker, Tick> tickerToTick = (s, p) -> {
        return new Tick(UniqueTimeMicros.uniqueTimeMicros(), 
                TradingUtils.toSymbol(BinanceAdapters.adaptSymbol(s).toString(), 
                TickerType.BINANCE),
                p.getLast().doubleValue(),
                p.getQuoteVolume().intValue());
    };
    
    private static BiFunction<String, BinanceTicker24h, Tick> binance24hourToTick = (s, p) -> {
        return new Tick(UniqueTimeMicros.uniqueTimeMicros(), 
                TradingUtils.toSymbol(BinanceAdapters.adaptSymbol(s).toString(), 
                TickerType.BINANCE),
                p.getLastPrice().doubleValue(),
                p.getQuoteVolume().intValue());
    };

    @Override
    public List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate,
            Duration duration) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean cancelOrder(Order order) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean modifyOrder(Order order) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Order> getOpenOrderDetails() {
        // TODO Auto-generated method stub
        return null;
    }

}
