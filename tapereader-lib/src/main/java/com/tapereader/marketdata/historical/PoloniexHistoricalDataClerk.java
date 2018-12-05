package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexChartData;
import org.knowm.xchange.poloniex.service.PoloniexChartDataPeriodType;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataService;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;
import com.tapereader.util.TradingUtils;

public class PoloniexHistoricalDataClerk implements HistoricalDataClerk {
    
    private static final Logger logger = LogManager.getLogger(PoloniexHistoricalDataClerk.class);
    
    private Exchange EXCHANGE;
    
    private PoloniexMarketDataServiceRaw marketDataService;

    @Override
    public List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate, Duration duration) {
        return Arrays.stream(getPoloniexChartData(security, startDate, endDate, duration))
        .map(c -> new Bar(
                        TradingUtils.toUnixTimeMicros(c.getDate().toInstant()),
                        TradingUtils.toSymbol(security.getSymbol(), TickerType.POLONIEX),
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
    
    private PoloniexChartData[] getPoloniexChartData(Security security, LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        PoloniexChartData[] chartData = null;
        try {
            CurrencyPair pair = new CurrencyPair(security.getSymbol());
            long start = TradingUtils.toUnixTimeSeconds(startTime);
            long end = TradingUtils.toUnixTimeSeconds(endTime);
            PoloniexChartDataPeriodType periodType = getPoloPeriodType(duration);
            chartData = marketDataService.getPoloniexChartData(pair, start, end, periodType);
        } catch (Exception e) {
            logger.error(e);
        }
        return chartData;
    }
    
    @Override
    public void init() {
        EXCHANGE = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName());
        marketDataService = (PoloniexMarketDataServiceRaw) (PoloniexMarketDataService) EXCHANGE.getMarketDataService();
    }

    @Override
    public void terminate() {
        
    }

}
