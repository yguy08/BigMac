package com.tapereader.tip;

import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerk;

public class TipClerk {
    
    private final MarketDataClerk marketDataClerk;
    
    private final HistoricalDataClerk historicalDataClerk;
    
    private Tip tip;
    
    public TipClerk(MarketDataClerk marketDataClerk, HistoricalDataClerk historicalDataClerk, Tip tip) {
        this.marketDataClerk = marketDataClerk;
        this.historicalDataClerk = historicalDataClerk;
        this.tip = tip;
    }

    /**
     * @return the tipName
     */
    public Tip getTip() {
        return tip;
    }

    /**
     * @param tipName the tipName to set
     */
    public void setTip(Tip tip) {
        this.tip = tip;
    }

    /**
     * @return the marketDataClerk
     */
    public MarketDataClerk getMarketDataClerk() {
        return marketDataClerk;
    }

    /**
     * @return the historicalDataClerk
     */
    public HistoricalDataClerk getHistoricalDataClerk() {
        return historicalDataClerk;
    }
}