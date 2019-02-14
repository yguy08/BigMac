package com.tapereader;

import com.tapereader.adapter.ExchangeAdapter;

public class ApplicationInjector {
    
    public static MainHelper injectMainHelper(ApplicationScope scope) {
        return new MainHelper(
                injectArgs(scope), 
                injectPropertiesFileName(scope),
                injectBinanceExchangeAdapter(scope));
    }
    
    public static String[] injectArgs(ApplicationScope scope) {
        return scope.getArgs();
    }
    
    public static String injectPropertiesFileName(ApplicationScope scope) {
        return scope.getPropertiesFileName();
    }
    
    public static ExchangeAdapter injectBinanceExchangeAdapter(ApplicationScope scope) {
        return scope.getBinanceExchangeAdapter();
    }

}
