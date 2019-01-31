package com.tapereader.tip;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.TimeSeries;

import com.tapereader.enumeration.TipType;
import com.tapereader.tip.buyhigh.BuyHigh;

public interface Tip {

    public static Tip makeFactory(TipType type) {
        switch (type) {
        case BUY_HIGH:
            return new BuyHigh();
        default:
            throw new IllegalArgumentException("TipType not supported.");
        }
    }

    public JFreeChart buildChart(TimeSeries series);

}
