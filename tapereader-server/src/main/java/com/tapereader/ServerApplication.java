package com.tapereader;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.MarketDataStreamingClerk;
import com.tapereader.config.BaseModule;
import com.tapereader.wire.ActiveMQBroker;

public class ServerApplication {

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new BaseModule());

        ActiveMQBroker broker = injector.getInstance(ActiveMQBroker.class);
        broker.init();

        MarketDataStreamingClerk quotron = injector.getInstance(MarketDataStreamingClerk.class);
        quotron.init();

        quotron.startRecording();
    }

}
