package com.tapereader.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;
import com.tapereader.enumeration.Side;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.model.Security;

public class TradingUtils {

    private static final int THOUSAND = 1000;
    
    /**
     * Private constructor. Static methods only. 
     */
    private TradingUtils() {
        
    }
    
    /**
     * Convert LocalDateTime to Unix Time microseconds
     * @param dateTime LocalDateTime to convert to microseconds
     * @return microseconds since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970.
     */
    public static long toUnixTimeMicros(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli() * THOUSAND;
    }
    
    /**
     * Convert Instant to Unix Time microseconds
     * @param instant Instant to convert to microseconds
     * @return microseconds since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970.
     */
    public static long toUnixTimeMicros(Instant instant) {
        return instant.toEpochMilli() * THOUSAND;
    }

    /**
     * Convert microseconds to seconds
     * @param microSeconds to convert to seconds
     * @return seconds
     */
    public static long microsToSeconds(long microSeconds) {
        return microSeconds / THOUSAND / THOUSAND;
    }

    /**
     * Convert microseconds to Instant
     * @param microSeconds to convert to instant
     * @return instant
     */
    public static Instant microsToInstant(long microSeconds) {
        return Instant.ofEpochSecond(microsToSeconds(microSeconds));
    }

    /**
     * Convert LocalDateTime to Unix Time seconds
     * @param dateTime LocalDateTime to convert to microseconds
     * @return seconds since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970.
     */
    public static long toUnixTimeSeconds(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }
    
    /**
     * Convert milliseconds to Microseconds
     * @param long milliseconds to convert to microseconds
     * @return seconds since 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970.
     */
    public static long millisToMicros(long millis) {
        return millis * THOUSAND;
    }
    
    public static boolean isUpdateBars(Bar lastBar, long compareSeconds) {
        if (lastBar == null) {
            return true;
        }
        long lastBarSeconds = TradingUtils.microsToSeconds(lastBar.getTimestamp());
        long now = Instant.now().getEpochSecond();
        return (now - lastBarSeconds > compareSeconds) ? true : false;
    }

    /**
     * Utility method to return the currency pair which form the security. 
     * It is effectively a string tokenizer with no validation for
     * currency or number of tokens or in fact the separator character.
     * 
     * input "BTC_USD", "_" will return ["BTC","USD"] input "BTC_USD", "/" will
     * return ["BTC_USD"] input "hello_GBP_USD", "_" will return
     * ["hello","GBP","USD"]
     * 
     * @param security
     * @param currencySeparator
     * @return String tokens for security which contains currencySeparator.
     */
    public static String[] splitCurrencyPair(String symbol, String currencySeparator) {
        return symbol.split(currencySeparator);
    }
    
    public static String toSymbol(String symbol, TickerType tickerType) {
        return symbol + ":" + tickerType.toString();
    }
    
    public static String toSymbol(Security security) {
        return security.getSymbol() + ":" + security.getBucketShop().getName();
    }
    
    public static String toMarket(String symbol, String currencySeparator) {
        return symbol.split(currencySeparator)[1];
    }
    
    public static String toTickerTypeStr(String symbol, String tickerSeparator) {
        return symbol.split(tickerSeparator)[1];
    }

    /**
     * Calculates the take profit price for a given trade based on the number of
     * pips desired for profit. When going long, the distance is calculated from
     * the ask price and from bid price when going short.
     * 
     * @param tickSize
     * @param signal
     * @param bidPrice
     * @param askPrice
     * @param pipsDesired
     * @return takeProfit price
     */
    public static double calculateTakeProfitPrice(double tickSize, Side signal, double bidPrice,
            double askPrice, int pipsDesired) {
        switch (signal) {
        case LONG:
            return askPrice + tickSize * pipsDesired;
        case SHORT:
            return bidPrice - tickSize * pipsDesired;
        default:
            return 0.0;
        }
    }

    /**
     * A utility method that is an alternative to having to write null and empty
     * checks for collections at various places in the code. Akin to
     * StringUtils.isEmpty() which returns true if input String is null or 0
     * length.
     * 
     * @param collection
     * @return boolean true if collection is null or is empty else false.
     * @see StringUtils#isEmpty(CharSequence)
     */
    public static final boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * A utility method that is an alternative to having to write null and empty
     * checks for maps at various places in the code. Akin to
     * StringUtils.isEmpty() which returns true if input String is null or 0
     * length.
     * 
     * @param map
     * @return boolean true if map is null or is empty else false.
     * @see StringUtils#isEmpty(CharSequence)
     */
    public static final boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}
