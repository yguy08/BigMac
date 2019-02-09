package com.tapereader.gui.utils;

import org.jfree.chart.ChartColor;
import org.jfree.chart.renderer.xy.CandlestickRenderer;

public class TRCandlestickRenderer extends CandlestickRenderer {
    
    public TRCandlestickRenderer() {
        setUseOutlinePaint(true);
        //setAutoWidthFactor(0.5);
        //setAutoWidthGap(0.5);
        setVolumePaint(ChartColor.WHITE);
        setDefaultToolTipGenerator(new TRToolTip());
    }

}
