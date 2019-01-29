package com.tapereader.tip.selllow;

import java.time.Duration;
import java.time.Instant;

import org.jfree.chart.JFreeChart;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.IsHighestRule;
import org.ta4j.core.trading.rules.IsLowestRule;
import org.ta4j.core.trading.rules.StopLossRule;

import com.tapereader.model.Security;
import com.tapereader.tip.SwingTip;

public class SellLow extends SwingTip {

    @Override
    public JFreeChart buildJFreeChart(Security security, Instant start, Duration barSize) {
        // TODO Auto-generated method stub
        return null;
    }

}
