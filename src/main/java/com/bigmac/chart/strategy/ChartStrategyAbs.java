package com.bigmac.chart.strategy;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.PrecisionNum;

import com.bigmac.trade.StrategyAnalysis;

public abstract class ChartStrategyAbs implements ChartStrategy {

    protected TimeSeries series;

    private TradingRecord tradingRecord;

    public ChartStrategyAbs(TimeSeries series) {
        this.series = series;
        init();
    }
    
    private void init() {
        Strategy strategy = new BaseStrategy(getName(), getEntryRule(), getExitRule(), 25);
        // ADD SIGNALS
        TimeSeriesManager seriesManager = new TimeSeriesManager(series);
        tradingRecord = seriesManager.run(strategy, getOrderType(), PrecisionNum.valueOf(1.0));
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

    public TradingRecord getTradingRecord() {
        return tradingRecord;
    }

}
