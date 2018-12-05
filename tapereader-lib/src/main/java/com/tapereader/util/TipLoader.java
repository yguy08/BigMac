package com.tapereader.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.ClerkModule;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.clerk.LookupClerk;
import com.tapereader.clerk.RecordClerk;
import com.tapereader.model.Tip;

public class TipLoader {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ClerkModule());
        JPAClerk service = injector.getInstance(JPAClerk.class);
        service.init();
        
        RecordClerk recordClerk = injector.getInstance(RecordClerk.class);
        recordClerk.init();
        
        LookupClerk lookupClerk = injector.getInstance(LookupClerk.class);
        lookupClerk.init();
        
        Tip buyHigh = new Tip();
        buyHigh.setName("Buy High");
        recordClerk.persist(buyHigh);
        
        Tip sellLow = new Tip();
        sellLow.setName("Sell Low");
        recordClerk.persist(sellLow);
        
        service.terminate();
    }

}
