package com.tapereader.gui.utils;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Day;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.ta4j.core.Bar;
import org.ta4j.core.Order;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.Trade;
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
import org.ta4j.core.num.PrecisionNum;

public class ChartUtils {

    public static final Stroke FATLINE = new BasicStroke(.9f);
    
    private static final CandlestickRenderer CANDLESTICK_RENDERER;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd");
    
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.of("UTC"));
    
    private static final RectangleInsets INSETS = new RectangleInsets(2, 2, 2, 2);
    
    static {
        CANDLESTICK_RENDERER = new CandlestickRenderer();
        CANDLESTICK_RENDERER.setUseOutlinePaint(true);
        CANDLESTICK_RENDERER.setAutoWidthFactor(0.5);
        CANDLESTICK_RENDERER.setAutoWidthGap(0.5);
        CANDLESTICK_RENDERER.setVolumePaint(ChartColor.WHITE);
        CANDLESTICK_RENDERER.setDefaultToolTipGenerator(new TRToolTip());
    }

    public static JFreeChart newCandleStickChart(TimeSeries timeseries) {
        String title = timeseries.getName();
        OHLCDataset ohlcDataset = ChartUtils.createOHLCDataset(title, timeseries);
        JFreeChart chart = ChartFactory.createCandlestickChart(title, "", "", ohlcDataset, false);
        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(CANDLESTICK_RENDERER);
        // Misc
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoRangeIncludesZero(false);
        NumberFormat format = title.contains("USDT") ? new DecimalFormat("#") : new DecimalFormat("#.########");
        numberAxis.setNumberFormatOverride(format);
        plot.setDomainPannable(true);
        plot.setBackgroundPaint(ChartColor.BLACK);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        // Chart
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(DATE_FORMAT);
        axis.setTimeZone(TIME_ZONE);
        axis.setLowerMargin(0.04);
        axis.setUpperMargin(0.04);
        chart.setPadding(INSETS);
        return chart;
    }

    /**
     * Builds a JFreeChart OHLC dataset from a ta4j time series.
     * 
     * @param series
     *            a time series
     * @return an Open-High-Low-Close dataset
     */
    private static OHLCDataset createOHLCDataset(String title, TimeSeries series) {
        final int nbBars = series.getBarCount();

        Date[] dates = new Date[nbBars];
        double[] opens = new double[nbBars];
        double[] highs = new double[nbBars];
        double[] lows = new double[nbBars];
        double[] closes = new double[nbBars];
        double[] volumes = new double[nbBars];

        for (int i = 0; i < nbBars; i++) {
            Bar bar = series.getBar(i);
            dates[i] = new Date(bar.getEndTime().toEpochSecond() * 1000);
            opens[i] = bar.getOpenPrice().doubleValue();
            highs[i] = bar.getMaxPrice().doubleValue();
            lows[i] = bar.getMinPrice().doubleValue();
            closes[i] = bar.getClosePrice().doubleValue();
            volumes[i] = bar.getVolume().doubleValue();
        }
        return new DefaultHighLowDataset(title, dates, highs, lows, opens, closes, volumes);
    }

    public static void prtStrategyAnalysis(TimeSeries series, TradingRecord tradingRecord) {
        // CASH FLOW
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        System.out.println("START CASH: " + cashFlow.getValue(series.getBeginIndex()));
        // Total profit
        TotalProfitCriterion totalProfit = new TotalProfitCriterion();
        System.out.println("Total profit: " + totalProfit.calculate(series, tradingRecord));
        // Number of bars
        System.out.println("Number of bars: " + new NumberOfBarsCriterion().calculate(series, tradingRecord));
        // Average profit (per bar)
        System.out
                .println("Average profit (per bar): " + new AverageProfitCriterion().calculate(series, tradingRecord));
        // Number of trades
        System.out.println("Number of trades: " + new NumberOfTradesCriterion().calculate(series, tradingRecord));
        // Profitable trades ratio
        System.out.println(
                "Profitable trades ratio: " + new AverageProfitableTradesCriterion().calculate(series, tradingRecord));
        // Maximum drawdown
        System.out.println("Maximum drawdown: " + new MaximumDrawdownCriterion().calculate(series, tradingRecord));
        // Reward-risk ratio
        System.out.println("Reward-risk ratio: " + new RewardRiskRatioCriterion().calculate(series, tradingRecord));
        // Total transaction cost
        System.out.println("Total transaction cost (from $1000): "
                + new LinearTransactionCostCriterion(5, 0.005).calculate(series, tradingRecord));
        // Buy-and-hold
        System.out.println("Buy-and-hold: " + new BuyAndHoldCriterion().calculate(series, tradingRecord));
        // Total profit vs buy-and-hold
        System.out.println("Custom strategy profit vs buy-and-hold strategy profit: "
                + new VersusBuyAndHoldCriterion(totalProfit).calculate(series, tradingRecord));
        // End cash
        System.out.println("END CASH: " + cashFlow.getValue(series.getEndIndex()));
        // ATR
        // Want to put this on the legend
        ATRIndicator atr = new ATRIndicator(series, 14);
        System.out.println("ATR: " + atr.getValue(series.getBarCount()-1));
    }
    
    public static void addBuySellSignals(TimeSeries series, Strategy strategy, JFreeChart chart, OrderType type) {
        TimeSeriesManager seriesManager = new TimeSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy, type, PrecisionNum.valueOf(1.0));
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
        ChartUtils.prtStrategyAnalysis(series, tradingRecord);
    }

}
