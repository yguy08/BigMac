package com.tapereader;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.config.BaseModule;
import com.tapereader.gui.TapeReaderGuiMain;
import com.tapereader.tip.TapeReader;
import com.tapereader.tip.SwingTip;

public class SwingApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        JPAClerk clerk = injector.getInstance(JPAClerk.class);
        clerk.init();
        
        TapeReader tapeReader = injector.getInstance(TapeReader.class);
        if (tapeReader.getLookupClerk() != null) {
            tapeReader.getLookupClerk().init();
        }
        if (tapeReader.getRecordClerk() != null) {
            tapeReader.getRecordClerk().init();
        }
        if (tapeReader.getHistoricalDataClerk() != null) {
            tapeReader.getHistoricalDataClerk().init();
        }
        if (tapeReader.getReceiver() != null) {
            tapeReader.getReceiver().init();
            tapeReader.getReceiver().receive(tapeReader);
        }
        if (tapeReader.getMarketDataClerk() != null) {
            tapeReader.getMarketDataClerk().init();
        }
        
        TapeReaderGuiMain demo = injector.getInstance(TapeReaderGuiMain.class);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                demo.init();
            }
        });
    }

    private static class AppModule extends AbstractModule {

        @Override
        public void configure() {
            install(new BaseModule("swing-application.properties"));
            bind(TapeReader.class).to(SwingTip.class);
            bind(TapeReaderGuiMain.class);
        }
    }

}
