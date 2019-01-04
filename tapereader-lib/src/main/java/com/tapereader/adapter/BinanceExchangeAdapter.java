package com.tapereader.adapter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Security;
import com.tapereader.util.TradingUtils;

public class BinanceExchangeAdapter implements ExchangeAdapter {
    
    private Exchange exchange;
    
    private BinanceMarketDataService marketDataService;
    
    @Inject(optional = true)
    @Named("bnc.volume.filter")
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

    @Override
    public List<Bar> getHistoricalBars(Security security, Instant startDate, Instant endDate, Duration duration) {
        return getBinanceKlines(security.getSymbol(), startDate, endDate, duration).stream()
                .map(c -> new Bar(c.getCloseTime(), 
                        TradingUtils.toSymbol(security.getSymbol(), TickerType.BINANCE), duration,
                        c.getOpenPrice().doubleValue(), c.getHighPrice().doubleValue(), c.getLowPrice().doubleValue(),
                        c.getClosePrice().doubleValue(), c.getVolume().intValue()))
                .collect(Collectors.toList());
    }
    
    private KlineInterval getKLineInterval(Duration duration) {
        KlineInterval kline = KlineInterval.d1;
        long millis = (long) duration.get(ChronoUnit.SECONDS) * 1000;
        for (KlineInterval type : KlineInterval.values()) {
            if (type.getMillis() == millis) {
                kline = type;
                break;
            }
        }
        return kline;
    }

    private List<BinanceKline> getBinanceKlines(String security, Instant startTime, Instant endTime,
            Duration duration) {
        List<BinanceKline> chartData = null;
        try {
            chartData = marketDataService.klines(new CurrencyPair(security), getKLineInterval(duration), null,
                    startTime.toEpochMilli(), endTime.toEpochMilli());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chartData;
    }

}
