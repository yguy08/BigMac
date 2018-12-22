package com.tapereader.clerk;

import java.util.List;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketData;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Line;
import com.tapereader.model.Security;
import com.tapereader.model.Tip;

public interface LookupClerk extends Clerk {

    Security findSecurity(String symbol, TickerType tickerType);
    
    List<Tick> getCurrentTicks();
    
    Bar getCurrentBar(Security security);

    List<Bar> getBars(Security security);
    
    List<Line> getAllLines();
    
    List<BucketShop> getAllBucketShops();
    
    BucketShop getBucketShop(TickerType tickerType);
    
    Tip findTipByName(String tipName);
    
    List<Tip> getAllTips();
    
    List<MarketData> getMarketData(int max);

}
