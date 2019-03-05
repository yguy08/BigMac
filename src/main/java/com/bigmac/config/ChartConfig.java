package com.bigmac.config;

public class ChartConfig {
    
    private static boolean includeZero = true;
    
    private static boolean addSMA = true;
    
    public static void setIncludeZero(boolean includeZero) {
        ChartConfig.includeZero = includeZero;
    }
    
    public static boolean getIncludeZero() {
        return includeZero;
    }

    /**
     * @return the addSMA
     */
    public static boolean isAddSMA() {
        return addSMA;
    }

    /**
     * @param addSMA the addSMA to set
     */
    public static void setAddSMA(boolean addSMA) {
        ChartConfig.addSMA = addSMA;
    }
}
