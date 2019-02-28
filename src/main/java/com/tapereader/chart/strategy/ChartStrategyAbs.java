package com.tapereader.chart.strategy;

import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Order;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.num.PrecisionNum;

import com.tapereader.chart.ChartUtils;
import com.tapereader.trade.StrategyAnalysis;

public abstract class ChartStrategyAbs implements ChartStrategy {

    protected TimeSeries series;

    private TradingRecord tradingRecord;
    
    private List<Trade> trades;

    public ChartStrategyAbs(TimeSeries series) {
        this.series = series;
        init();
    }
    
    private void init() {
        Strategy strategy = new BaseStrategy(getName(), getEntryRule(), getExitRule(), 25);
        // ADD SIGNALS
        TimeSeriesManager seriesManager = new TimeSeriesManager(series);
        tradingRecord = seriesManager.run(strategy, getOrderType(), PrecisionNum.valueOf(1.0));
        trades = tradingRecord.getTrades();
    }

    /**
     * @return the series
     */
    public TimeSeries getSeries() {
        return series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(TimeSeries series) {
        this.series = series;
        init();
    }

    public String getStrategyAnalysis() {
        StrategyAnalysis analysis = new StrategyAnalysis(series, getTradingRecord());
        return analysis.getStrategyAnalysis();
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public TradingRecord getTradingRecord() {
        return tradingRecord;
    }
    
    public JFreeChart buildChart() {
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(series);
        XYPlot plot = chart.getXYPlot();
        
        // Adding markers to plot
        for (Trade trade : getTrades()) {
            // Buy signal
            double buySignalBarTime = new FixedMillisecond(
                    Date.from(series.getBar(trade.getEntry().getIndex()).getEndTime().toInstant()))
                            .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalBarTime = new FixedMillisecond(
                    Date.from(series.getBar(trade.getExit().getIndex()).getEndTime().toInstant())).getLastMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime, ChartColor.RED, ChartUtils.FATLINE);
            plot.addDomainMarker(sellMarker);
        }
        Order last = tradingRecord.getLastOrder();
        if (last != null && (getOrderType() == OrderType.BUY && last.isBuy())) {
            double buySignalBarTime = new FixedMillisecond(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant()))
                    .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(buyMarker);
        } else if (last != null && (getOrderType() == OrderType.SELL && last.isSell())) {
            double sellSignalBarTime = new FixedMillisecond(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant())).getLastMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime);
            sellMarker.setPaint(ChartColor.GREEN);
            sellMarker.setStroke(ChartUtils.FATLINE);
            plot.addDomainMarker(sellMarker);
        }
        return chart;
    }

}
