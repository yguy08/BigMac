package com.tapereader.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringJoiner;

import org.ta4j.core.TimeSeries;
import org.ta4j.core.TradingRecord;
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
import org.ta4j.core.num.Num;

public class StrategyAnalysis {
    
    private TimeSeries series;
    
    private TradingRecord tradingRecord;
    
    private StringJoiner sb;
    
    public StrategyAnalysis(TimeSeries series, TradingRecord tradingRecord) {
        this.series = series;
        this.tradingRecord = tradingRecord;
        this.sb = new StringJoiner("\n");
    }
    
    public String getStrategyAnalysis() {
        // CASH FLOW
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        sb.add("START CASH: " + cashFlow.getValue(series.getBeginIndex()));
        
        // Total profit
        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
        String nxt = scale(totalProfit.calculate(series, tradingRecord), 5);
        sb.add("Total profit: " + nxt);
        
        // Number of bars
        nxt = scale(new NumberOfBarsCriterion().calculate(series, tradingRecord), 5);
        sb.add("Number of bars: " + nxt);
        
        // Average profit (per bar)
        nxt = scale(new AverageProfitCriterion().calculate(series, tradingRecord), 5);
        sb.add("Average profit (per bar): " + nxt);
        
        // Number of trades
        sb.add("Number of trades: " + new NumberOfTradesCriterion().calculate(series, tradingRecord));
        
        // Profitable trades ratio
        sb.add("Profitable trades ratio: " + new AverageProfitableTradesCriterion().calculate(series, tradingRecord));
        
        // Maximum drawdown
        nxt = scale(new MaximumDrawdownCriterion().calculate(series, tradingRecord), 5);
        sb.add("Maximum drawdown: " + nxt);
        
        // Reward-risk ratio
        nxt = scale(new RewardRiskRatioCriterion().calculate(series, tradingRecord), 5);
        sb.add("Reward-risk ratio: " + nxt);
        
        // Total transaction cost
        nxt = scale(new LinearTransactionCostCriterion(5, 0.005).calculate(series, tradingRecord), 5);
        sb.add("Total transaction cost (from $1000): "
                + nxt);
        
        // Buy-and-hold
        nxt = scale(new BuyAndHoldCriterion().calculate(series, tradingRecord), 5);
        sb.add("Buy-and-hold: " + nxt);
        
        // Total profit vs buy-and-hold
        nxt = scale(new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord), 5);
        sb.add("Custom strategy profit vs buy-and-hold strategy profit: "
                + nxt);
        
        // End cash
        nxt = scale(cashFlow.getValue(series.getEndIndex()), 5);
        sb.add("END CASH: " + nxt);
        
        // ATR
        ATRIndicator atr = new ATRIndicator(series, 14);
        nxt = scale(atr.getValue(series.getBarCount()-1), 8);
        sb.add("ATR: " + nxt);
        
        return sb.toString();
    }
    
    public String scale(Num num, int scale) {
        if (!num.isNaN()) {
            BigDecimal bd = new BigDecimal(num.doubleValue());
            return bd.setScale(scale, RoundingMode.HALF_UP).toPlainString();
        } else {
            return "NaN";
        }
    }
}
