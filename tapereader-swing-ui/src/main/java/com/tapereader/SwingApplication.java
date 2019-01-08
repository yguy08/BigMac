package com.tapereader;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.tapereader.adapter.BinanceExchangeAdapter;
import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.PoloniexExchangeAdapter;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.clerk.MarketDataClerk;
import com.tapereader.clerk.MarketDataClerkImpl;
import com.tapereader.config.BaseModule;
import com.tapereader.enumeration.TickerType;
import com.tapereader.gui.TapeReaderGuiMain;
import com.tapereader.tip.TapeReader;
import com.tapereader.tip.SwingTip;

public class SwingApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        JPAClerk clerk = injector.getInstance(JPAClerk.class);
        clerk.init();
        
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
            install(new BaseModule());
            bind(TapeReader.class).to(SwingTip.class);
            bind(TapeReaderGuiMain.class);
        }
    }

}
