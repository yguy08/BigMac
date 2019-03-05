package com.bigmac.enumeration;

import java.util.HashMap;

/**
 * @author wendre01
 *
 */
public enum TickerType {

    BINANCE("BNC"),
    POLONIEX("POLO"),
    CPRO("CPRO");
    
    private static final HashMap<String, TickerType> codeToEnumMapping = new HashMap<String, TickerType>(TickerType.values().length);
    
    static {
        for (TickerType ticker : TickerType.values()) {
            codeToEnumMapping.put(ticker.getCode(), ticker);
        }
    }
    
    private String code;
    
    TickerType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static TickerType enumOf(String codeValue) {
        if (codeToEnumMapping.containsKey(codeValue)) {
            return codeToEnumMapping.get(codeValue);
        }
        return null;
    }

}
