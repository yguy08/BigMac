package com.bigmac.engine;

import java.time.Duration;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class EngineConfig {

    public Engine engine() {
        com.espertech.esper.client.Configuration config = new com.espertech.esper.client.Configuration();
        config.configure("tapereader-esper.cfg.xml");
        config.addVariable("barsize", Long.class, Duration.ofDays(1).getSeconds());
        EPServiceProvider epService = EPServiceProviderManager.getProvider("core", config);
        Engine engine = new EngineImpl(epService);
        return engine;
    }

}
