package com.tapereader.clerk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.event.MarketDataHandler;

public class BinanceTicker implements Ticker, Runnable {

    private MarketDataHandler eventHandler;

    private AtomicBoolean running = new AtomicBoolean(false);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    @Inject
    @Named("bncExchangeClerk")
    private ExchangeAdapter clerk;

    @Inject(optional = true)
    @Named("bnc.throttle")
    private String throttle = "3600";
    
    @Inject
    @Named("initdelay")
    private String initDelay = "60";

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
        executor.scheduleWithFixedDelay(this, Integer.parseInt(initDelay), Integer.parseInt(throttle), TimeUnit.SECONDS);
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

}
