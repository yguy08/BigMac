package com.tapereader;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.gui.AppModule;
import com.tapereader.gui.TapeReaderGuiMain;

public class Application {

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

}
