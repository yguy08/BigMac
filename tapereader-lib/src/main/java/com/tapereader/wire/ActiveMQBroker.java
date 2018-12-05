package com.tapereader.wire;

import org.apache.activemq.broker.BrokerService;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ActiveMQBroker implements BrokerClerk {
    
    private final String BROKER_URL;
    
    private final BrokerService broker;
    
    @Inject
    private ActiveMQBroker(@Named("mqbrokerurl") String brokerUrl) {
        this.BROKER_URL = brokerUrl;
        this.broker = new BrokerService();
    }

    @Override
    public void init() {
        try {
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(BROKER_URL);
            broker.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void terminate() {
        if (broker != null) {
            try {
                broker.stop();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
