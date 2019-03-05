package com.bigmac.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.ta4j.core.Bar;
import org.ta4j.core.Indicator;
import org.ta4j.core.Order;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import com.bigmac.chart.strategy.ChartStrategy;
import com.bigmac.config.ChartConfig;
import com.bigmac.gui.chart.TRCandlestickRenderer;

public class ChartUtils {

    private static final Stroke FATLINE = new BasicStroke(.9f);

    private static TRCandlestickRenderer TrCandleStickRenderer;

    private static final String SHORT_DATE_FORMAT = "MM/dd";
    
    private static final String DATE_TIME_FORMAT = "MM/dd HH:mm";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(SHORT_DATE_FORMAT);

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.of("UTC"));

    private static JFreeChart chart;
    
    public static JFreeChart newCandleStickChart(TimeSeries series) {
        String title = series.getName();
        OHLCDataset ohlcDataset = ChartUtils.createOHLCDataset(title, series);
        chart = ChartFactory.createCandlestickChart(title, "Date", "Price", ohlcDataset, false);
        Duration barSize = series.getFirstBar().getTimePeriod();
        String dateFormat = barSize.toDays() > 0 ? SHORT_DATE_FORMAT : DATE_TIME_FORMAT;
        
        // Candlestick rendering
        TrCandleStickRenderer = new TRCandlestickRenderer(dateFormat);
        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(TrCandleStickRenderer);
        
        // Price Axis
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoRangeIncludesZero(ChartConfig.getIncludeZero());
        NumberFormat format = title.contains("USDT") ? new DecimalFormat("#") : new DecimalFormat("#.########");
        numberAxis.setNumberFormatOverride(format);
        
        // Date Axis
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(DATE_FORMAT);
        axis.setTimeZone(TIME_ZONE);
        
        // Misc
        plot.setDomainPannable(true);
        plot.setBackgroundPaint(ChartColor.BLACK);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        
        // remove if issue
        return chart;
    }

    public static JFreeChart buildChart(ChartStrategy strategy) {
        TimeSeries series = strategy.getSeries();
        TradingRecord tradingRecord = strategy.getTradingRecord();
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(series);
        XYPlot plot = chart.getXYPlot();
        // Adding markers to plot
        for (Trade trade : tradingRecord.getTrades()) {
            // Buy signal
            double buySignalBarTime = new FixedMillisecond(
                    Date.from(series.getBar(trade.getEntry().getIndex()).getEndTime().toInstant()))
                            .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(0, buyMarker, Layer.FOREGROUND);
            // Sell signal
            double sellSignalBarTime = new FixedMillisecond(
                    Date.from(series.getBar(trade.getExit().getIndex()).getEndTime().toInstant())).getLastMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime, ChartColor.RED, ChartUtils.FATLINE);
            plot.addDomainMarker(sellMarker);
        }
        Order last = tradingRecord.getLastOrder();
        if (last != null && (OrderType.BUY.equals(strategy.getOrderType()) && last.isBuy())) {
            double buySignalBarTime = new FixedMillisecond(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant()))
                    .getLastMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime, ChartColor.GREEN, ChartUtils.FATLINE);
            plot.addDomainMarker(buyMarker);
        } else if (last != null && (OrderType.SELL.equals(strategy.getOrderType()) && last.isSell())) {
            double sellSignalBarTime = new FixedMillisecond(Date.from(series.getBar(last.getIndex()).getEndTime().toInstant())).getLastMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime);
            sellMarker.setPaint(ChartColor.GREEN);
            sellMarker.setStroke(ChartUtils.FATLINE);
            plot.addDomainMarker(sellMarker);
        }

        if (ChartConfig.isAddSMA()) {
            // short
            TimeSeriesCollection xyDataset = buildChartTimeSeries(series, 
                    new SMAIndicator(new ClosePriceIndicator(series), 20), "SMA");
            // Additional dataset
            int index = 1;
            plot.setDataset(index, xyDataset);
            plot.mapDatasetToRangeAxis(index, 0);
            XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
            renderer2.setSeriesFillPaint(index, ChartColor.RED);
            renderer2.setSeriesPaint(index, ChartColor.RED);
            renderer2.setUseFillPaint(true);
            plot.setRenderer(index, renderer2);
        }
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
    
    private static TimeSeriesCollection  buildChartTimeSeries(TimeSeries barseries, Indicator<Num> indicator, String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i = 0; i < barseries.getBarCount(); i++) {
            Bar bar = barseries.getBar(i);
            chartTimeSeries.add(new FixedMillisecond(new Date(bar.getEndTime().toInstant().toEpochMilli())),
                    indicator.getValue(i).doubleValue());
        }
        dataset.addSeries(chartTimeSeries);
        return dataset;
    }

}
