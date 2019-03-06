package com.bigmac.adapter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;
import com.bigmac.marketdata.Tick;

public class BinanceExchangeAdapter extends XchangeAdapterAbs {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BinanceExchangeAdapter.class);
    
    public BinanceExchangeAdapter() {
        super(BinanceExchange.class.getName());
    }

    @Override
    public Tick getCurrentTick(String symbol) {
        try {
            BinanceTicker24h ticker = ((BinanceMarketDataServiceRaw) getMarketDataService()).ticker24h(new CurrencyPair(symbol));
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
            List<BinanceKline> chartData = ((BinanceMarketDataServiceRaw) getMarketDataService()).klines(new CurrencyPair(security), kline, 1000,
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
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = new ArrayList<>();
        try {
            List<BinanceTicker24h> binanceTicks = ((BinanceMarketDataServiceRaw) getMarketDataService()).ticker24h();
            for (BinanceTicker24h bncTick : binanceTicks) {
                Tick tick = binanceTicker24hToTick(bncTick);
                if (tick.getTimestamp().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
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
