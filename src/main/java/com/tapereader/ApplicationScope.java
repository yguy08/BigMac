package com.tapereader;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.bnc.BinanceExchangeAdapter;

public class ApplicationScope {

    private final String[] args;
    
    private final String propertiesFileName;

    public ApplicationScope(String[] args, String propertiesFileName) {
        this.args = args;
        this.propertiesFileName = propertiesFileName;
    }

    public String[] getArgs() {
        return args;
    }
    
    public String getPropertiesFileName() {
        return propertiesFileName;
    }
    
    public ExchangeAdapter getBinanceExchangeAdapter() {
        return new BinanceExchangeAdapter();
    }

}
