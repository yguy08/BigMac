package com.tapereader.gui.chart;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

import org.jfree.chart.ChartColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.ta4j.core.TimeSeries;

public class TRChart {
    
    private JFreeChart chart;
    
    private DateAxis timeAxis;
    
    private NumberAxis valueAxis;
    
    private CandlestickRenderer renderer ;
    
    private XYPlot plot;
    
    public TRChart(String title, TimeSeries series) {
        timeAxis = new DateAxis("Date");
        valueAxis = new NumberAxis("Price");
        plot = new XYPlot(ChartUtils.createOHLCDataset(title, series), timeAxis, valueAxis, null);
        renderer = new CandlestickRenderer();
        plot.setRenderer(renderer);
        chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        
        // Candlestick rendering
        renderer.setUseOutlinePaint(true);
        renderer.setAutoWidthFactor(0.5);
        renderer.setAutoWidthGap(0.5);
        renderer.setVolumePaint(ChartColor.WHITE);
        renderer.setDefaultToolTipGenerator(new TrToolTipGenerator());

        // Misc
        valueAxis.setAutoRangeIncludesZero(false);
        NumberFormat format = title.contains("USDT") ? new DecimalFormat("#") : new DecimalFormat("#.########");
        valueAxis.setNumberFormatOverride(format);
        
        plot.setDomainPannable(true);
        plot.setBackgroundPaint(ChartColor.BLACK);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        
        // Chart
        timeAxis.setDateFormatOverride(new SimpleDateFormat("MM/dd"));
        timeAxis.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        timeAxis.setLowerMargin(0.04);
        timeAxis.setUpperMargin(0.04);
        
        chart.setPadding(new RectangleInsets(2, 2, 2, 2));
    }
    
    public JFreeChart getChart() {
        return chart;
    }
}
