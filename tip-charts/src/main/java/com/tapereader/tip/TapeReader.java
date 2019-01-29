package com.tapereader.tip;

import com.google.inject.Inject;
import com.tapereader.clerk.Clerk;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.HistoricalDataClerk;
import com.tapereader.config.Configuration;
import com.tapereader.dao.LookupClerk;
import com.tapereader.dao.RecordClerk;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Tip;

public class TapeReader implements Clerk {
    
    private Configuration configuration;
    
    private LookupClerk lookupClerk;
    
    private RecordClerk recordClerk;
    
    private HistoricalDataClerk newspaper;
    
    private MarketDataClerk marketDataClerk;
    
    private String tipName;

    public Configuration getConfiguration() {
        return configuration;
    }
    
    @Inject(optional=true)
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Tip getTip() {
        return lookupClerk.findTipByName(getTipName());
    }

    @Inject(optional=true)
    public void setTipName(String tipName) {
        this.tipName = tipName;
    }
    
    public String getTipName() {
        return tipName;
    }

    public LookupClerk getLookupClerk() {
        return lookupClerk;
    }

    @Inject(optional=true)
    public void setLookupClerk(LookupClerk lookupClerk) {
        this.lookupClerk = lookupClerk;
    }

    public MarketDataClerk getMarketDataClerk() {
        return marketDataClerk;
    }
    
    @Inject(optional=true)
    public void setMarketDataClerk(MarketDataClerk marketDataClerk) {
        this.marketDataClerk = marketDataClerk;
    }

    public RecordClerk getRecordClerk() {
        return recordClerk;
    }

    @Inject(optional=true)
    public void setRecordClerk(RecordClerk recordClerk) {
        this.recordClerk = recordClerk;
    }
    
    public HistoricalDataClerk getHistoricalDataClerk() {
        return newspaper;
    }

    @Inject(optional=true)
    public void setHistoricalDataClerk(HistoricalDataClerk newspaper) {
        this.newspaper = newspaper;
    }
    
    public void readTheTape() {
        
    }

    @Override
    public void init() {

    }

    @Override
    public void terminate() {
        
    }
    
    public void onTick(Tick tick) {
        
    }
}