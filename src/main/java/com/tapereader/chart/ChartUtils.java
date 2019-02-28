package com.tapereader.chart;

import java.awt.BasicStroke;
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
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.ta4j.core.Bar;
import org.ta4j.core.TimeSeries;
import com.tapereader.gui.chart.TRCandlestickRenderer;

public class ChartUtils {

    public static final Stroke FATLINE = new BasicStroke(.9f);
    
    private static CandlestickRenderer CANDLESTICK_RENDERER;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd");
    
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneId.of("UTC"));
    
    private static final RectangleInsets INSETS = new RectangleInsets(2, 2, 2, 2);

    public static JFreeChart newCandleStickChart(TimeSeries timeseries) {
        String title = timeseries.getName();
        OHLCDataset ohlcDataset = ChartUtils.createOHLCDataset(title, timeseries);
        JFreeChart chart = ChartFactory.createCandlestickChart(title, "Date", "Price", ohlcDataset, false);
        XYPlot plot = chart.getXYPlot();
        Duration barSize = timeseries.getFirstBar().getTimePeriod();
        String dateFormat = null;
        if (barSize.toDays() > 0) {
            dateFormat = "MM/dd";
        } else {
            dateFormat = "MM/dd HH:mm";
        }
        CANDLESTICK_RENDERER = new TRCandlestickRenderer(dateFormat);
        plot.setRenderer(CANDLESTICK_RENDERER);
        // Misc
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        //numberAxis.setAutoRangeIncludesZero(false);
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

}
