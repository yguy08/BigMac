package com.tapereader.adapter.polo;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexMarketData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataService;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.reference.Security;
import com.tapereader.util.TradingUtils;
import com.tapereader.util.UniqueTimeMicros;

public class PoloniexExchangeAdapter implements ExchangeAdapter {
    
    private Exchange exchange;
    
    private PoloniexMarketDataService marketDataService;

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

    @Override
    public List<Bar> getHistoricalBars(String security, Instant startDate, Instant endDate, Duration duration) {
        return Arrays.stream(getPoloniexChartData(security, startDate, endDate, duration))
        .map(c -> new Bar(
                        c.getDate().toInstant().toEpochMilli(),
                        TradingUtils.toSymbol(security, TickerType.POLONIEX),
                        duration, 
                        c.getOpen().doubleValue(), 
                        c.getHigh().doubleValue(), 
                        c.getLow().doubleValue(), 
                        c.getClose().doubleValue(), 
                        c.getVolume().intValue()))
        .collect(Collectors.toList());
    }
    
    private PoloniexChartDataPeriodType getPoloPeriodType(Duration duration) {
        PoloniexChartDataPeriodType poloPeriodType = PoloniexChartDataPeriodType.PERIOD_86400;
        int seconds = (int) duration.get(ChronoUnit.SECONDS);
        for(PoloniexChartDataPeriodType type : PoloniexChartDataPeriodType.values()) {
            if(type.getPeriod() == seconds) {
                poloPeriodType = type;
            }
        }
        return poloPeriodType;
    }
    
    private PoloniexChartData[] getPoloniexChartData(String security, Instant startTime, Instant endTime, Duration duration) {
        PoloniexChartData[] chartData = null;
        try {
            CurrencyPair pair = new CurrencyPair(security);
            long start = startTime.getEpochSecond();
            long end = endTime.getEpochSecond();
            PoloniexChartDataPeriodType periodType = getPoloPeriodType(duration);
            chartData = marketDataService.getPoloniexChartData(pair, start, end, periodType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chartData;
    }

}
