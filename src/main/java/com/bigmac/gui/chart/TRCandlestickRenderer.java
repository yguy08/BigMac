package com.bigmac.gui.chart;

import org.jfree.chart.ChartColor;
import org.jfree.chart.renderer.xy.CandlestickRenderer;

public class TRCandlestickRenderer extends CandlestickRenderer {
    
    public TRCandlestickRenderer(String dateFormat) {
        setUseOutlinePaint(true);
        setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        setVolumePaint(ChartColor.WHITE);
        setDefaultToolTipGenerator(new TRToolTip(dateFormat));
    }
    
    public TRCandlestickRenderer(String dateFormat, ChartColor volumePaint) {
        this(dateFormat);
        setVolumePaint(volumePaint);
    }

}
