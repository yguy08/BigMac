package com.tapereader;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.config.Config;
import com.tapereader.enumeration.MarketType;
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
        
        Config config = new Config();
        config.setBarSize(Duration.ofDays(1));
        config.setDefaultSymbol("BTC/USDT");
        config.setDefaultTip(TipType.BUY_HIGH);
        config.setLookback(100);
        config.setTickerType(TickerType.BINANCE);
        config.setMarketType(MarketType.BTC);
        
        TipClerk tipClerk = new TipClerk(config, marketDataClerk, historicalDataClerk, Tip.makeFactory(TipType.BUY_HIGH));
        
        TRGuiMain app = new TRGuiMain(tipClerk);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.createAndShowGui();
            }
        });
    }

}
