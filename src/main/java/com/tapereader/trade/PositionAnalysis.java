package com.tapereader.trade;

import java.math.BigDecimal;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.ATRIndicator;

public class PositionAnalysis {
    
    private TimeSeries series;
    
    public PositionAnalysis(TimeSeries series) {
        this.series = series;
    }
    
    public BigDecimal getATR() {
        ATRIndicator atr = new ATRIndicator(series, 14);
        return new BigDecimal(atr.getValue(series.getBarCount()-1).doubleValue());
    }

}
