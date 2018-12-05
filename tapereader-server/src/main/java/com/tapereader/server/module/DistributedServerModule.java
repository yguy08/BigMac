package com.tapereader.server.module;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.tapereader.annotation.Boot;
import com.tapereader.event.DistributedMarketDataHandler;
import com.tapereader.event.MarketDataHandler;
import com.tapereader.wire.ActiveMQBroker;
import com.tapereader.wire.BrokerClerk;
import com.tapereader.wire.DefaultReceiver;
import com.tapereader.wire.DefaultTransmitter;
import com.tapereader.wire.MarketDataMessageProtocol;
import com.tapereader.wire.MessageProtocol;
import com.tapereader.wire.Receiver;
import com.tapereader.wire.Transmitter;

@Boot(moduleName = "distributed")
public class DistributedServerModule extends AbstractModule {
    
    private final EventBus eventBus = new EventBus();

    @Override
    protected void configure() {
        // Messaging
        bind(BrokerClerk.class).to(ActiveMQBroker.class);
        bind(Receiver.class).to(DefaultReceiver.class);
        bind(Transmitter.class).to(DefaultTransmitter.class);
        bind(MessageProtocol.class).to(MarketDataMessageProtocol.class);
        
        // Market Data
        bind(EventBus.class).toInstance(eventBus);
        bind(MarketDataHandler.class).to(DistributedMarketDataHandler.class);
    }
}
