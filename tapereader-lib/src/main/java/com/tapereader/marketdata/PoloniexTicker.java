package com.tapereader.marketdata;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexMarketData;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataService;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.enumeration.TickerType;
import com.tapereader.event.MarketDataHandler;
import com.tapereader.model.Security;
import com.tapereader.util.TradingUtils;
import com.tapereader.util.UniqueTimeMicros;

public class PoloniexTicker implements Ticker, Runnable {
    
    private Exchange exchange;
    
    private PoloniexMarketDataService marketDataService;
    
    private MarketDataHandler eventHandler;
    
    private AtomicBoolean running = new AtomicBoolean(false);
    
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    
    @Inject
    @Named("poloniex.throttle.seconds")
    private String throttle = "3600";
    
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
    
    @Inject
    public PoloniexTicker(MarketDataHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void init() {
        exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class);
        marketDataService = (PoloniexMarketDataService) exchange.getMarketDataService();
    }

    @Override
    public void terminate() {
        stopTicker();
    }

    @Override
    public void startTicker() {
        executor.scheduleWithFixedDelay(this, 120, Integer.parseInt(throttle), TimeUnit.SECONDS);
    }

    @Override
    public void stopTicker() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        try {
            ((PoloniexMarketDataServiceRaw) marketDataService)
            .getAllPoloniexTickers()
            .entrySet()
            .stream()
            .filter(t -> t.getValue().getBaseVolume().intValue() > 0)
            .map(t -> translateTo(t.getKey(), t.getValue()))
            .forEach(t -> eventHandler.onMarketEvent(t));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private final Tick translateTo(String symbol, PoloniexMarketData chartData) {
        return tickSupplier.apply(symbol, chartData);
    }

}
