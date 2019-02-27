package com.tapereader.gui.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;

public class TRToolTip implements XYToolTipGenerator {
    
    private String dateFormat;
    
    public TRToolTip(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String generateToolTip(XYDataset dataset, int series, int item) {
        try {
            DefaultHighLowDataset defaultDataSet = ((DefaultHighLowDataset) dataset);
            StringBuilder stringBuilder = new StringBuilder();
            
            double open = defaultDataSet.getOpen(series, item).doubleValue();
            double high = defaultDataSet.getHigh(series, item).doubleValue();
            double low = defaultDataSet.getLow(series, item).doubleValue();
            double close = defaultDataSet.getClose(series, item).doubleValue();
            
            Number date = dataset.getX(series, item);
            String key = dataset.getSeriesKey(series).toString();
            String f = key.contains("USDT") ? "%.2f" : "%.8f";
            
            stringBuilder.append(String.format("<html><p style='color:#0000ff;'> %s </p>", dataset.getSeriesKey(series)));
            stringBuilder.append("Date: " + millisToDateString(date.longValue())+ "<br/>");
            stringBuilder.append(String.format("Open: " + f + "<br/>", open));
            stringBuilder.append(String.format("High: " + f + "<br/>", high));
            stringBuilder.append(String.format("Low: " + f + "<br/>", low));
            stringBuilder.append(String.format("Close: " + f + "<br/>", close));
            stringBuilder.append("</html>");
            return stringBuilder.toString();
        } catch (RuntimeException e) {
            return "ERROR";
        }
    }
    
    private String millisToDateString(long millis) {
        String ret = LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(dateFormat));
        return ret;
    }
}
