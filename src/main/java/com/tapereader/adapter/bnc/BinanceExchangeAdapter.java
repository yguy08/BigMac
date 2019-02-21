package com.tapereader.adapter.bnc;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public class BinanceExchangeAdapter implements ExchangeAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BinanceExchangeAdapter.class);
    
    private BinanceExchange exchange;
    
    private BinanceMarketDataService marketDataService;
    
    private String minVol = "50";
    
    private boolean started = false;
    
    public BinanceExchangeAdapter() {

    }

    @Override
    public Tick getCurrentTick(String symbol) {
        try {
            BinanceTicker24h ticker = marketDataService.ticker24h(new CurrencyPair(symbol));
            return binanceTicker24hToTick(ticker);
        } catch (IOException e) {
            LOGGER.error("BinanceExchangeAdapter.getCurrentTicks: Error connecting to Binance Exchange.", e);
        } catch (Exception e) {
            LOGGER.error("BinanceExchangeAdapter.getCurrentTicks: Error getting current ticks.", e);
        }
        return null;
    }

    @Override
    public List<Bar> getHistoricalBars(String security, Instant startDate, Instant endDate, Duration duration) {
        if (security == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("security, start date or end date must not be null.");
        }
        List<Bar> bars = new ArrayList<>();
        KlineInterval kline = getKLineInterval(duration);
        try {
            List<BinanceKline> chartData = marketDataService.klines(new CurrencyPair(security), kline, null,
                    startDate.toEpochMilli(), endDate.toEpochMilli());
            for (BinanceKline bncBar : chartData) {
                long millis = bncBar.getCloseTime();
                String symbol = bncBar.getCurrencyPair().toString();
                double open = bncBar.getOpenPrice().doubleValue();
                double high = bncBar.getHighPrice().doubleValue();
                double low = bncBar.getLowPrice().doubleValue();
                double close = bncBar.getClosePrice().doubleValue();
                int vol = bncBar.getVolume().intValue();
                bars.add(new Bar(millis, symbol, TickerType.BINANCE, open, high, low, close, vol, duration));
            }
        } catch (Exception e) {
            LOGGER.error("BinanceExchangeAdapter.getHistoricalBars: Error getting historical bars.");
        }
        return bars;
    }
    
    private KlineInterval getKLineInterval(Duration duration) {
        KlineInterval kline = KlineInterval.d1;
        long millis = duration.get(ChronoUnit.SECONDS) * 1000;
        for (KlineInterval type : KlineInterval.values()) {
            if (type.getMillis().longValue() == millis) {
                kline = type;
                break;
            }
        }
        return kline;
    }
    
    @Override
    public boolean init() {
        if (!started) {
            exchange = (BinanceExchange) ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
            marketDataService = (BinanceMarketDataService) exchange.getMarketDataService();
            started = true;
        }
        return started;
    }

    @Override
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = new ArrayList<>();
        try {
            List<BinanceTicker24h> binanceTicks = marketDataService.ticker24h();
            for (BinanceTicker24h bncTick : binanceTicks) {
                Tick tick = binanceTicker24hToTick(bncTick);
                if (Instant.ofEpochMilli(tick.getTimestamp()).isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
                    continue;
                }
                ticks.add(tick);
            }
        } catch (IOException e) {
            LOGGER.error("BinanceExchangeAdapter.getCurrentTicks: Error connecting to Binance Exchange.", e);
        } catch (Exception e) {
            LOGGER.error("BinanceExchangeAdapter.getCurrentTicks: Error getting current ticks.", e);
        }
        return ticks;
    }
    
    private Tick binanceTicker24hToTick(BinanceTicker24h ticker) {
        long millis = ticker.getCloseTime().toInstant().toEpochMilli();
        String symbol = BinanceAdapters.adaptSymbol(ticker.getSymbol()).toString();
        double last = ticker.getLastPrice().doubleValue();
        int vol = ticker.getQuoteVolume().intValue();
        double percent = ticker.getPriceChangePercent().doubleValue();
        return new Tick(millis, symbol, TickerType.BINANCE, last, vol, percent);
    }

}
