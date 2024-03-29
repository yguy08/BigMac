package com.bigmac.adapter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.coinbasepro.dto.marketdata.CoinbaseProCandle;
import org.knowm.xchange.coinbasepro.dto.marketdata.CoinbaseProProductTicker;
import org.knowm.xchange.coinbasepro.service.CoinbaseProMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigmac.domain.Symbol;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;
import com.bigmac.marketdata.Tick;

public class CProExchangeAdapter extends XchangeAdapterAbs {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CProExchangeAdapter.class);
    
    private final String[] SYMBOLS = new String[] {"BTC/USD", "ETH/USD", "LTC/USD"};
    
    private final List<Tick> ticks = new ArrayList<>();
    
    public CProExchangeAdapter() {
        super(CoinbaseProExchange.class.getName());
    }

    @Override
    public Tick getCurrentTick(String symbol) {
        try {
            CurrencyPair cPair = new CurrencyPair(symbol);
            CoinbaseProProductTicker ticker = ((CoinbaseProMarketDataServiceRaw) getMarketDataService()).getCoinbaseProProductTicker(cPair);
            return coinBaseTickerToTick(ticker, cPair);
        } catch (Exception e) {
            LOGGER.error("CProExchangeAdapter.getCurrentTicks: Error getting current ticks.");
        }
        return null;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration) {
        if (symbol == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("symbol, start date or end date must not be null.");
        }
        List<Bar> bars = new ArrayList<>();
        try {
            CoinbaseProCandle[] candles = ((CoinbaseProMarketDataServiceRaw) getMarketDataService()).getCoinbaseProHistoricalCandles(new CurrencyPair(symbol), startDate.toString(), 
                    endDate.toString(), String.valueOf(duration.getSeconds()));
            if (candles != null) {
                CoinbaseProCandle firstCandle = candles[0];
                CoinbaseProCandle lastCandle = candles[candles.length -1];
                if (firstCandle.getTime().after(lastCandle.getTime())) {
                    Collections.reverse(Arrays.asList(candles));
                }
                for (CoinbaseProCandle candle : candles) {
                    Instant timestamp = candle.getTime().toInstant();
                    double open = candle.getOpen().doubleValue();
                    double high = candle.getHigh().doubleValue();
                    double low = candle.getLow().doubleValue();
                    double close = candle.getClose().doubleValue();
                    int volume = candle.getVolume().intValue();
                    bars.add(new Bar(timestamp, new Symbol(symbol, TickerType.CPRO), open, high, low, close, volume, duration));
                }
            }
        } catch (IOException e) {
            LOGGER.error("CProExchangeAdapter.getHistoricalBars: Error getting current bars.");
        }
        return bars;
    }

    @Override
    public List<Tick> getCurrentTicks() {
        if (ticks.isEmpty()) {
            for (String pair : SYMBOLS) {
                try {
                    CurrencyPair symbol = new CurrencyPair(pair);
                    CoinbaseProProductTicker ticker = ((CoinbaseProMarketDataServiceRaw) getMarketDataService()).getCoinbaseProProductTicker(symbol);
                    ticks.add(coinBaseTickerToTick(ticker, symbol));
                } catch (Exception e) {
                    LOGGER.error("CProExchangeAdapter.getCurrentTicks: Error getting current ticks.");
                }
            }
        }
        return ticks;
    }
    
    private Tick coinBaseTickerToTick(CoinbaseProProductTicker ticker, CurrencyPair pair) {
        Instant millis = Instant.now();
        double last = ticker.getPrice().doubleValue();
        int vol = (int) (ticker.getVolume().doubleValue() * ticker.getPrice().doubleValue());
        return new Tick(millis, new Symbol(pair.toString(), TickerType.CPRO), last, vol, 0);
    }

}
