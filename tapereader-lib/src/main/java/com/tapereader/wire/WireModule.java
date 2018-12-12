package com.tapereader.wire;

import javax.inject.Named;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class WireModule extends AbstractModule {
    
    private final EventBus eventBus = new EventBus();
    
    @Override 
    public void configure() {
        bind(Receiver.class).to(DefaultReceiver.class);
        bind(Transmitter.class).to(DefaultTransmitter.class);
        bind(MessageProtocol.class).to(MarketDataMessageProtocol.class);
        bind(EventBus.class).toInstance(eventBus);
    }
    
    @Provides
    public ActiveMQBroker activeMQBroker(@Named("mqbrokerurl") String brokerUrl) {
        return new ActiveMQBroker(brokerUrl);
    }

}
