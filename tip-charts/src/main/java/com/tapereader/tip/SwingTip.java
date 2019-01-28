package com.tapereader.tip;

import java.time.Duration;
import java.time.Instant;

import org.jfree.chart.JFreeChart;

import com.tapereader.model.Security;

public abstract class SwingTip extends TapeReader {
    
    public abstract JFreeChart buildJFreeChart(Security security, Instant start, Duration barSize);
    
}
