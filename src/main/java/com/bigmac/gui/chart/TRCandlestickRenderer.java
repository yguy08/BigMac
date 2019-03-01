package com.bigmac.gui.chart;

import org.jfree.chart.ChartColor;
import org.jfree.chart.renderer.xy.CandlestickRenderer;

public class TRCandlestickRenderer extends CandlestickRenderer {
    
    public TRCandlestickRenderer(String dateFormat) {
        setUseOutlinePaint(true);
        setVolumePaint(ChartColor.WHITE);
        setDefaultToolTipGenerator(new TRToolTip(dateFormat));
    }

}
