package com.tapereader.wire;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

public class WireModule extends AbstractModule {
    
    private final EventBus eventBus = new EventBus();
    
    @Override 
    public void configure() {
        bind(BrokerClerk.class).to(ActiveMQBroker.class);
        bind(Receiver.class).to(DefaultReceiver.class);
        bind(Transmitter.class).to(DefaultTransmitter.class);
        bind(MessageProtocol.class).to(MarketDataMessageProtocol.class);
        bind(EventBus.class).toInstance(eventBus);
    }

}
