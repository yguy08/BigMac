package com.tapereader.adapter.cpro;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.coinbasepro.dto.marketdata.CoinbaseProCandle;
import org.knowm.xchange.coinbasepro.dto.marketdata.CoinbaseProProductTicker;
import org.knowm.xchange.coinbasepro.service.CoinbaseProMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public class CProExchangeAdapter implements ExchangeAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CProExchangeAdapter.class);
    
    private CoinbaseProExchange exchange;
    
    private CoinbaseProMarketDataService marketDataService;
    
    private final String[] SYMBOLS = new String[] {"BTC/USD", "ETH/USD", "LTC/USD"};
    
    private final List<Tick> ticks = new ArrayList<>();

    @Override
    public void init() {
        exchange = ExchangeFactory.INSTANCE.createExchange(CoinbaseProExchange.class);
        marketDataService = (CoinbaseProMarketDataService) exchange.getMarketDataService();
    }

    @Override
    public List<Tick> getCurrentTicks() {
        if (ticks.isEmpty()) {
            for (String pair : SYMBOLS) {
                try {
                    CurrencyPair symbol = new CurrencyPair(pair);
                    CoinbaseProProductTicker ticker = marketDataService.getCoinbaseProProductTicker(symbol);
                    long millis = Instant.now().toEpochMilli();
                    double last = ticker.getPrice().doubleValue();
                    int vol = ticker.getVolume().intValue();
                    ticks.add(new Tick(millis, symbol.toString(), TickerType.CPRO, last, vol, 0));
                } catch (Exception e) {
                    LOGGER.error("CProExchangeAdapter.getCurrentTicks: Error getting current ticks.");
                }
            }
        }
        return ticks;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration) {
        if (symbol == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("symbol, start date or end date must not be null.");
        }
        List<Bar> bars = new ArrayList<>();
        try {
            CoinbaseProCandle[] candles = marketDataService.getCoinbaseProHistoricalCandles(new CurrencyPair(symbol), startDate.toString(), 
                    endDate.toString(), String.valueOf(duration.getSeconds()));
            if (candles != null) {
                CoinbaseProCandle firstCandle = candles[0];
                CoinbaseProCandle lastCandle = candles[candles.length -1];
                if (firstCandle.getTime().after(lastCandle.getTime())) {
                    Collections.reverse(Arrays.asList(candles));
                }
            }
            for (CoinbaseProCandle candle : candles) {
                long timestamp = candle.getTime().toInstant().toEpochMilli();
                double open = candle.getOpen().doubleValue();
                double high = candle.getHigh().doubleValue();
                double low = candle.getLow().doubleValue();
                double close = candle.getClose().doubleValue();
                int volume = candle.getVolume().intValue();
                bars.add(new Bar(timestamp, symbol, open, high, low, close, volume, duration));
            }
        } catch (IOException e) {
            LOGGER.error("CProExchangeAdapter.getHistoricalBars: Error getting current bars.");
        }
        return bars;
    }

}
