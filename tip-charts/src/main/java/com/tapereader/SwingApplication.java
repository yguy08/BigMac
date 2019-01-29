package com.tapereader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.gui.TRGuiMain;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.MarketDataClerkImpl;
import com.tapereader.marketdata.historical.HistoricalDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerkImpl;
import com.tapereader.tip.Tip;
import com.tapereader.tip.TipClerk;

public class SwingApplication {

    public static void main(String[] args) {
        ExchangeAdapter bncAdapter = ExchangeAdapter.makeFactory(TickerType.BINANCE);
        bncAdapter.init();
        
        ExchangeAdapter poloAdapter = ExchangeAdapter.makeFactory(TickerType.POLONIEX);
        poloAdapter.init();
        
        Map<String, ExchangeAdapter> adapterMap = new HashMap<>();
        adapterMap.put(TickerType.BINANCE.toString(), bncAdapter);
        adapterMap.put(TickerType.POLONIEX.toString(), poloAdapter);
        
        MarketDataClerk marketDataClerk = new MarketDataClerkImpl(adapterMap);
        HistoricalDataClerk historicalDataClerk = new HistoricalDataClerkImpl(adapterMap);
        
        TipClerk tipClerk = new TipClerk(marketDataClerk, historicalDataClerk, Tip.makeFactory(TipType.BUY_HIGH));
        
        TRGuiMain app = new TRGuiMain(tipClerk);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.runGui();
            }
        });
    }

}
