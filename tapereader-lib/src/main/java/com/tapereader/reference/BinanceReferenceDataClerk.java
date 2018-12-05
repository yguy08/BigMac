package com.tapereader.reference;

import java.util.List;
import java.util.stream.Collectors;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;

import com.tapereader.model.Security;

public class BinanceReferenceDataClerk implements ReferenceDataClerk {

    private Exchange exchange;
    
    @Override
    public List<Security> getSecurities() {
        return exchange
                        .getExchangeSymbols()
                        .parallelStream()
                        .map(p -> new Security(p.toString()))
                        .collect(Collectors.toList());
    }

    @Override
    public void init() {
        exchange = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class);
    }

    @Override
    public void terminate() {
        
    }

}
