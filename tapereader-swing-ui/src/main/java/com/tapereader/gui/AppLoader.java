package com.tapereader.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.JPAClerk;

public class AppLoader {

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
