package com.tapereader.marketdata.historical;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;
import com.tapereader.util.TradingUtils;

public class BinanceHistoricalDataClerk implements HistoricalDataClerk {

    private static final Logger logger = LogManager.getLogger(BinanceHistoricalDataClerk.class);

    private Exchange EXCHANGE;

    private BinanceMarketDataServiceRaw marketDataService;

    @Override
    public List<Bar> getHistoricalBars(Security security, LocalDateTime startDate, LocalDateTime endDate,
            Duration duration) {
        return getBinanceKlines(security.getSymbol(), startDate, endDate, duration).stream()
                .map(c -> new Bar(TradingUtils.millisToMicros(c.getCloseTime()), 
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

    private List<BinanceKline> getBinanceKlines(String security, LocalDateTime startTime, LocalDateTime endTime,
            Duration duration) {
        List<BinanceKline> chartData = null;
        try {
            chartData = marketDataService.klines(new CurrencyPair(security), getKLineInterval(duration), null,
                    TradingUtils.toUnixTimeSeconds(startTime) * 1000, TradingUtils.toUnixTimeSeconds(endTime) * 1000);
        } catch (Exception e) {
            logger.error(e);
        }
        return chartData;
    }

    @Override
    public void init() {
        EXCHANGE = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
        marketDataService = (BinanceMarketDataServiceRaw) (BinanceMarketDataService) EXCHANGE.getMarketDataService();
    }

    @Override
    public void terminate() {

    }

}
