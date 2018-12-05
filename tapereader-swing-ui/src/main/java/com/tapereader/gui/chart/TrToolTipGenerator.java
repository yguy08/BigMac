package com.tapereader.gui.chart;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;

public class TrToolTipGenerator implements XYToolTipGenerator {

    @Override
    public String generateToolTip(XYDataset dataset, int series, int item) {
        try {
            Number open = ((DefaultHighLowDataset) dataset).getOpen(series, item);
            Number high = ((DefaultHighLowDataset) dataset).getHigh(series, item);
            Number low = ((DefaultHighLowDataset) dataset).getLow(series, item);
            Number close = ((DefaultHighLowDataset) dataset).getClose(series, item);
            Number date = dataset.getX(series, item);
            StringBuilder stringBuilder = new StringBuilder();
            String key = dataset.getSeriesKey(series).toString();
            String f = key.contains("USDT") ? "%.2f" : "%.8f";
            stringBuilder.append(String.format("<html><p style='color:#0000ff;'> %s </p>", dataset.getSeriesKey(series)));
            stringBuilder.append("Date: " + ChartUtils.millisToDateString(date.longValue())+ "<br/>");
            stringBuilder.append(String.format("Open: " + f + "<br/>", open.doubleValue()));
            stringBuilder.append(String.format("High: " + f + "<br/>", high.doubleValue()));
            stringBuilder.append(String.format("Low: " + f + "<br/>", low.doubleValue()));
            stringBuilder.append(String.format("Close: " + f + "<br/>", close.doubleValue()));
            stringBuilder.append("</html>");
            return stringBuilder.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

}