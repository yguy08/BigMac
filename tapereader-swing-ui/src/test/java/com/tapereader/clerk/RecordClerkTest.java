package com.tapereader.clerk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.historical.BinanceHistoricalDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerk;
import com.tapereader.model.Security;

public class RecordClerkTest {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ClerkModule());
        JPAClerk service = injector.getInstance(JPAClerk.class);
        service.init();
        
        RecordClerk recordClerk = injector.getInstance(RecordClerk.class);
        LookupClerk lookupClerk = injector.getInstance(LookupClerk.class);
        
        Security security = lookupClerk.findSecurity("BTC/USDT", TickerType.BINANCE);
        
        HistoricalDataClerk clerk = new BinanceHistoricalDataClerk();
        clerk.init();
        
        List<Bar> bars = clerk.getHistoricalBars(security, LocalDateTime.now().minusDays(100), 
                LocalDateTime.now(), Duration.ofDays(1));
        
        recordClerk.updateBars(security, bars);
        
        List<Bar> lookupBars = lookupClerk.getBars(security);
        
        for (Bar b : lookupBars) {
            System.out.println(b);
        }
        
        service.terminate();
    }

}
