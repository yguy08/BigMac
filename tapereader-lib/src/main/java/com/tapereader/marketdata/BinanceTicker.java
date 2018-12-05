package com.tapereader.marketdata;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.clerk.ExchangeClerk;
import com.tapereader.event.MarketDataHandler;
import com.tapereader.model.Security;

public class BinanceTicker implements Ticker, Runnable {

    private MarketDataHandler eventHandler;

    private AtomicBoolean running = new AtomicBoolean(false);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    @Inject
    private ExchangeClerk clerk;

    @Inject(optional = true)
    @Named("binance.throttle.seconds")
    private String throttle = "3600";

    @Inject
    public BinanceTicker(MarketDataHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void init() {
        clerk.init();
        eventHandler.init();
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
            clerk.getCurrentTicks().stream().forEach(t -> eventHandler.onMarketEvent(t));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void subscribe(String tipName, Security security) {

    }

}
