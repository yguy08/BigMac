package com.tapereader.chart.strategy.buyhigh;

import java.util.Date;
import java.util.List;
import org.jfree.chart.ChartColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Order;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.chart.ChartUtils;
import com.tapereader.chart.strategy.ChartStrategy;
import com.tapereader.enumeration.TipType;
import com.tapereader.util.StrategyAnalysis;

public class BuyHigh implements ChartStrategy {

    private TimeSeries series;
    
    private OrderType type = OrderType.BUY;

    private TradingRecord tradingRecord;

    @Override
    public JFreeChart buildChart(TimeSeries series) {
        this.series = series;
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        // Going long if the close price goes above the max
        Rule entryRule = new IsHighestRule(closePrices, 25);
        // Exit if the close price goes below the min or stop loss
        Rule exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
        Strategy strategy = new BaseStrategy(TipType.BUY_HIGH.toString(), entryRule, exitRule, 25);
        
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(series);
        
        // ADD SIGNALS
        TimeSeriesManager seriesManager = new TimeSeriesManager(series);
        tradingRecord = seriesManager.run(strategy, type, PrecisionNum.valueOf(1.0));
        List<Trade> trades = tradingRecord.getTrades();
        XYPlot plot = chart.getXYPlot();
        
        // Adding markers to plot
        for (Trade trade : trades) {
            // Buy signal
            double buySignalBarTime = new Day(
                    Date.from(series.getBar(trade.getEntry().getIndex()).getEndTime().toInstant()))
                            .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalBarTime = new Day(
                    Date.from(series.getBar(trade.getExit().getIndex()).getEndTime().toInstant())).getLastMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime, ChartColor.RED, ChartUtils.FATLINE);
            plot.addDomainMarker(sellMarker);
        }
        Order last = tradingRecord.getLastOrder();
        if (last != null && (type == OrderType.BUY && last.isBuy())) {
            double buySignalBarTime = new Day(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant()))
                    .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(buyMarker);
        } else if (last != null && (type == OrderType.SELL && last.isSell())) {
            double sellSignalBarTime = new Day(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant())).getLastMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime);
            sellMarker.setPaint(ChartColor.GREEN);
            sellMarker.setStroke(ChartUtils.FATLINE);
            plot.addDomainMarker(sellMarker);
        }
        return chart;
    }
    
    public String getStrategyAnalysis() {
        StrategyAnalysis analysis = new StrategyAnalysis(series, tradingRecord);
        return analysis.getStrategyAnalysis();
    }

    @Override
    public void addBuySellSignalsToChart() {
        // TODO Auto-generated method stub
        
    }

}
