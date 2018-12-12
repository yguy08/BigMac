package com.tapereader.adapter;

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
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Tick;
import com.tapereader.util.TradingUtils;

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
            .map(t -> binance24hourToTick.apply(t.getSymbol(), t))
            .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    private static BiFunction<String, BinanceTicker24h, Tick> binance24hourToTick = (s, p) -> {
        return new Tick(p.getCloseTime().toInstant().toEpochMilli(), 
                TradingUtils.toSymbol(BinanceAdapters.adaptSymbol(s).toString(), 
                TickerType.BINANCE),
                p.getLastPrice().doubleValue(),
                p.getQuoteVolume().intValue());
    };

}
