package com.tapereader;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.MarketDataStreamingClerk;
import com.tapereader.config.BaseModule;
import com.tapereader.wire.ActiveMQBroker;

public class ServerApplication {

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new BaseModule("server-application.properties"));
        
        ActiveMQBroker broker = injector.getInstance(ActiveMQBroker.class);
        broker.init();
        
        MarketDataStreamingClerk clerk = injector.getInstance(MarketDataStreamingClerk.class);
        clerk.init();
        clerk.startRecording();
    }

}
