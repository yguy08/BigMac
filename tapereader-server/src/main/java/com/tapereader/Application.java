package com.tapereader;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.ticker.Quotron;
import com.tapereader.ticker.TickerServerModule;
import com.tapereader.wire.ActiveMQBroker;

public class Application {

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new TickerServerModule());
        
        ActiveMQBroker broker = injector.getInstance(ActiveMQBroker.class);
        broker.init();
        
        Quotron quotron = injector.getInstance(Quotron.class);
        quotron.init();
        
        quotron.startRecording();
    }

}
