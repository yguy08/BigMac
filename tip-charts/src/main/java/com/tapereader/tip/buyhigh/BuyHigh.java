package com.tapereader.tip.buyhigh;

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
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitCriterion;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.BuyAndHoldCriterion;
import org.ta4j.core.analysis.criteria.LinearTransactionCostCriterion;
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.analysis.criteria.NumberOfBarsCriterion;
import org.ta4j.core.analysis.criteria.NumberOfTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.enumeration.TipType;
import com.tapereader.gui.utils.ChartUtils;
import com.tapereader.tip.Tip;

public class BuyHigh implements Tip {
    
    private TimeSeries series;
    
    private ClosePriceIndicator closePrices;
    
    private Rule entryRule;
    
    private Rule exitRule;
    
    private Strategy strategy;
    
    private JFreeChart chart;
    
    private OrderType orderType = OrderType.BUY;
    
    private TimeSeriesManager seriesManager;
    
    private TradingRecord tradingRecord;

    @Override
    public JFreeChart buildChart(TimeSeries series) {
        this.series = series;
        closePrices = new ClosePriceIndicator(series);
        // Going long if the close price goes above the max
        entryRule = new IsHighestRule(closePrices, 25);
        // Exit if the close price goes below the min or stop loss
        exitRule = new IsLowestRule(closePrices, 11).or(new StopLossRule(closePrices, PrecisionNum.valueOf(30)));
        strategy = new BaseStrategy(TipType.BUY_HIGH.toString(), entryRule, exitRule, 25);
        
        //Building chart datasets
        chart = ChartUtils.newCandleStickChart(series.getName(), series);
        
        // ADD SIGNALS
        seriesManager = new TimeSeriesManager(series);
        tradingRecord = seriesManager.run(strategy, orderType, PrecisionNum.valueOf(1.0));
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
        if (last != null) {
            double buySignalBarTime = new Day(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant()))
                    .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(buyMarker);
        }
        return chart;
    }
    
    public String getStrategyAnalysis() {
        // CASH FLOW
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        StringBuilder sb = new StringBuilder();
        sb.append("START CASH: " + cashFlow.getValue(series.getBeginIndex()));
        // Total profit
        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
        sb.append("Total profit: " + totalProfit.calculate(series, tradingRecord));
        // Number of bars
        sb.append("Number of bars: " + new NumberOfBarsCriterion().calculate(series, tradingRecord));
        // Average profit (per bar)
        sb.append("Average profit (per bar): " + new AverageProfitCriterion().calculate(series, tradingRecord));
        // Number of trades
        sb.append("Number of trades: " + new NumberOfTradesCriterion().calculate(series, tradingRecord));
        // Profitable trades ratio
        sb.append(
                "Profitable trades ratio: " + new AverageProfitableTradesCriterion().calculate(series, tradingRecord));
        // Maximum drawdown
        sb.append("Maximum drawdown: " + new MaximumDrawdownCriterion().calculate(series, tradingRecord));
        // Reward-risk ratio
        sb.append("Reward-risk ratio: " + new RewardRiskRatioCriterion().calculate(series, tradingRecord));
        // Total transaction cost
        sb.append("Total transaction cost (from $1000): "
                + new LinearTransactionCostCriterion(5, 0.005).calculate(series, tradingRecord));
        // Buy-and-hold
        sb.append("Buy-and-hold: " + new BuyAndHoldCriterion().calculate(series, tradingRecord));
        // Total profit vs buy-and-hold
        sb.append("Custom strategy profit vs buy-and-hold strategy profit: "
                + new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord));
        // End cash
        sb.append("END CASH: " + cashFlow.getValue(series.getEndIndex()));
        // ATR
        // Want to put this on the legend
        ATRIndicator atr = new ATRIndicator(series, 14);
        sb.append("ATR: " + atr.getValue(series.getBarCount()-1));
        return sb.toString();
    }

}
