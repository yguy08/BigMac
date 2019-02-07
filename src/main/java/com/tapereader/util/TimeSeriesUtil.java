package com.tapereader.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.marketdata.Bar;

public class TimeSeriesUtil {

    public static TimeSeries buildTimeSeries(String seriesName, List<Bar> bars) {
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName(seriesName).build();
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        return series;
    }

}
