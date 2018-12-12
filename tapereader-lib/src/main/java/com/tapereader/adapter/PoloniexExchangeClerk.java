package com.tapereader.adapter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexMarketData;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataService;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Tick;
import com.tapereader.util.TradingUtils;
import com.tapereader.util.UniqueTimeMicros;

public class PoloniexExchangeClerk implements ExchangeClerk {
    
    private Exchange exchange;
    
    private PoloniexMarketDataService marketDataService;
    
    @Inject
    @Named("poloniex.throttle.seconds")
    private String throttle = "3600";

    @Override
    public void init() {
        exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class);
        marketDataService = (PoloniexMarketDataService) exchange.getMarketDataService();
    }

    @Override
    public void terminate() {
        
    }

    @Override
    public List<Tick> getCurrentTicks() {
        try {
            return ((PoloniexMarketDataServiceRaw) marketDataService)
            .getAllPoloniexTickers()
            .entrySet()
            .stream()
            .map(t -> translateTo(t.getKey(), t.getValue()))
            .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    private static Function<String, String> splitPair = s -> {
        String[] split = TradingUtils.splitCurrencyPair(s, "_");
        return split[1] + "/" + split[0];
    };
    
    private static BiFunction<String, PoloniexMarketData, Tick> tickSupplier = (s, p) -> {
        return new Tick(UniqueTimeMicros.uniqueTimeMicros(), 
                TradingUtils.toSymbol(splitPair.apply(s), TickerType.POLONIEX),
                p.getLast().doubleValue(), 
                p.getBaseVolume().intValue());
    };
    
    private final Tick translateTo(String symbol, PoloniexMarketData chartData) {
        return tickSupplier.apply(symbol, chartData);
    }

}
