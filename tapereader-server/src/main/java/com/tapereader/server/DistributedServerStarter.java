package com.tapereader.server;

import com.google.inject.Inject;
import com.tapereader.boot.BootStarter;
import com.tapereader.marketdata.Quotron;
import com.tapereader.wire.BrokerClerk;

public class DistributedServerStarter implements BootStarter {
    
    private BrokerClerk broker;
    
    private Quotron quotron;
    
    @Inject
    private DistributedServerStarter(BrokerClerk broker, Quotron quotron) {
        this.broker = broker;
        this.quotron = quotron;
    }
    
    public void run() {
        broker.init();
        quotron.init();
        quotron.startRecording();
    }

    public static void main(String[] args) throws Exception {
        ServerLoader.load(DistributedServerStarter.class, args);
    }

}
