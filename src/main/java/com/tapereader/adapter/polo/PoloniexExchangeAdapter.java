package com.tapereader.adapter.polo;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexMarketData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public class PoloniexExchangeAdapter implements ExchangeAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexExchangeAdapter.class);
    
    private PoloniexExchange exchange;
    
    private PoloniexMarketDataService marketDataService;

    public PoloniexExchangeAdapter() {
        
    }
    
    @Override
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = new ArrayList<>();
        try {
            Map<String, PoloniexMarketData> tickMap = marketDataService.getAllPoloniexTickers();
            for (Map.Entry<String, PoloniexMarketData> entry : tickMap.entrySet()) {
                String symbol = rawSymbolToCurrencyPairStr(entry.getKey());
                PoloniexMarketData marketData = entry.getValue();
                long millis = Instant.now().toEpochMilli();
                double last = marketData.getLast().doubleValue();
                int vol = marketData.getBaseVolume().intValue();
                ticks.add(new Tick(millis, symbol, last, vol));
            }
        } catch (Exception e) {
            LOGGER.error("PoloniexExchangeAdapter.getCurrentTicks: Error getting current ticks.");
        }
        return ticks;
    }

    @Override
    public List<Bar> getHistoricalBars(String symbol, Instant startDate, Instant endDate, Duration duration) {
        if (symbol == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("symbol, start date or end date must not be null.");
        }
        List<Bar> bars = new ArrayList<>();
        PoloniexChartDataPeriodType poloPeriodType = getPoloPeriodType(duration);
        try {
            PoloniexChartData[] chartData = marketDataService.getPoloniexChartData(new CurrencyPair(symbol), 
                    startDate.getEpochSecond(), endDate.getEpochSecond(), poloPeriodType);
            for (PoloniexChartData data : chartData) {
                long millis = data.getDate().toInstant().toEpochMilli();
                double open = data.getOpen().doubleValue();
                double high = data.getHigh().doubleValue();
                double low = data.getLow().doubleValue();
                double close = data.getClose().doubleValue();
                int vol = data.getVolume().intValue();
                bars.add(new Bar(millis, symbol, open, high, low, close, vol, duration));
            }
        } catch (Exception e) {
            LOGGER.error("PoloniexExchangeAdapter.getHistoricalBars: Error getting current bars.");
        }
        return bars;
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
    
    private String rawSymbolToCurrencyPairStr(String rawSymbol) {
        String[] split = rawSymbol.split("_");
        return split[1] + "/" + split[0];
    }
    
    @Override
    public void init() {
        exchange = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class);
        marketDataService = (PoloniexMarketDataService) exchange.getMarketDataService();
    }

}
