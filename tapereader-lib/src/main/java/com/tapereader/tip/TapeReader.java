package com.tapereader.tip;

import com.google.inject.Inject;
import com.tapereader.clerk.Clerk;
import com.tapereader.clerk.LookupClerk;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.RecordClerk;
import com.tapereader.config.Configuration;
import com.tapereader.marketdata.Tick;
import com.tapereader.marketdata.historical.Newspaper;
import com.tapereader.model.Tip;
import com.tapereader.wire.Receiver;

public class TapeReader implements Clerk {
    
    private Configuration configuration;
    
    private LookupClerk lookupClerk;
    
    private RecordClerk recordClerk;
    
    private Newspaper newspaper;
    
    private Receiver receiver;
    
    private Tip tip;
    
    private String tipName;
    
    private MarketDataClerk marketDataClerk;

    public Configuration getConfiguration() {
        return configuration;
    }
    
    @Inject(optional=true)
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Tip getTip() {
        return tip;
    }

    @Inject(optional=true)
    public void setTip(Tip tip) {
        this.tip = tip;
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
    
    public Newspaper getNewspaper() {
        return newspaper;
    }

    @Inject(optional=true)
    public void setNewspaper(Newspaper newspaper) {
        this.newspaper = newspaper;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    @Inject(optional=true)
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
    
    public void readTheTape() {
        
    }

    @Override
    public void init() {
        if (getLookupClerk() != null) {
            getLookupClerk().init();
        }
        if (getRecordClerk() != null) {
            getRecordClerk().init();
        }
        if (getNewspaper() != null) {
            getNewspaper().init();
        }
        if (getReceiver() != null) {
            getReceiver().init();
            getReceiver().receive(this);
        }
        if (getMarketDataClerk() != null) {
            getMarketDataClerk().init();
        }
    }

    @Override
    public void terminate() {
        
    }
    
    public void onTick(Tick tick) {
        
    }
}