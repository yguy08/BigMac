package com.bigmac.adapter;

import java.util.function.Supplier;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XchangeAdapterAbs implements ExchangeAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(XchangeAdapterAbs.class);

    private Supplier<Exchange> exchangeSupplier = this::createAndCacheExchange;

    private final String exchangeClassName;

    public XchangeAdapterAbs(String exchangeClassName) {
        this.exchangeClassName = exchangeClassName;
        LOGGER.info(exchangeClassName + " Exchange created.");
    }

    protected Exchange getExchange() {
        return exchangeSupplier.get();
    }
    
    protected MarketDataService getMarketDataService() {
        return exchangeSupplier.get().getMarketDataService();
    }

    private synchronized Exchange createAndCacheExchange() {

        class ExchangeSupplier implements Supplier<Exchange> {

            private Exchange exchangeInstance;

            private ExchangeSupplier(String exchangeClassName) {
                exchangeInstance = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);
            }

            @Override
            public Exchange get() {
                return exchangeInstance;
            }
        }
        if (!ExchangeSupplier.class.isInstance(exchangeSupplier)) {
            exchangeSupplier = new ExchangeSupplier(exchangeClassName);
        }
        return exchangeSupplier.get();
    }
}
