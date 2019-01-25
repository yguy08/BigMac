package com.tapereader;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tapereader.adapter.BinanceExchangeAdapter;
import com.tapereader.adapter.PoloniexExchangeAdapter;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.config.BaseModule;
import com.tapereader.dao.LookupClerk;
import com.tapereader.dao.RecordClerk;
import com.tapereader.gui.TRGuiMain;
import com.tapereader.tip.SwingTip;

public class SwingApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BaseModule("application.properties"), new AppModule());
        JPAClerk clerk = injector.getInstance(JPAClerk.class);
        clerk.init();
        
        BinanceExchangeAdapter bncAdapter = injector.getInstance(BinanceExchangeAdapter.class);
        bncAdapter.init();
        
        PoloniexExchangeAdapter poloAdapter = injector.getInstance(PoloniexExchangeAdapter.class);
        poloAdapter.init();
        
        LookupClerk lookupClerk = injector.getInstance(LookupClerk.class);
        lookupClerk.init();
        
        RecordClerk recordClerk = injector.getInstance(RecordClerk.class);
        recordClerk.init();
        
        SwingTip tip = injector.getInstance(SwingTip.class);
        tip.init();
        
        TRGuiMain app = injector.getInstance(TRGuiMain.class);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.runGui();
            }
        });
    }

    private static class AppModule extends AbstractModule {

        @Override
        public void configure() {
            bind(SwingTip.class).in(Singleton.class);
            bind(TRGuiMain.class).in(Singleton.class);
        }
    }

}
