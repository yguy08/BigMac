package com.bigmac.domain;

import com.bigmac.enumeration.TickerType;

public class Symbol {
    
    public final String base;
    
    public final String counter;
    
    public final TickerType ticker;
    
    private static final String BASE_COUNTER_SEP = "/";
    
    private static final String COUNTER_TICKER_SEP = ":";
    
    public Symbol(String base, String counter, TickerType ticker) {
        this.base = base;
        this.counter = counter;
        this.ticker = ticker;
    }
    
    public Symbol(String base, String counter, String ticker) {
      this(base, counter, TickerType.enumOf(ticker));
    }
    
    /**
     * Parse currency pair from a string in the same format as returned by toString() method - ABC/XYZ
     */
    public Symbol(String symbol) {
      int splitSymbol = symbol.indexOf(BASE_COUNTER_SEP);
      if (splitSymbol < 1) {
        throw new IllegalArgumentException(
            "Could not parse currency pair from '" + symbol + "'");
      }
      int splitTicker = symbol.indexOf(COUNTER_TICKER_SEP);
      if (splitTicker < 1) {
        throw new IllegalArgumentException(
            "Could not parse currency pair from '" + symbol + "'");
      }
      String base = symbol.substring(0, splitSymbol);
      String counter = symbol.substring(splitSymbol + 1, splitTicker);
      String ticker = symbol.substring(splitTicker + 1);

      this.base = base;
      this.counter = counter;
      this.ticker = TickerType.enumOf(ticker);
    }
    
    public Symbol(String symbol, TickerType ticker) {
        int splitSymbol = symbol.indexOf(BASE_COUNTER_SEP);
        if (splitSymbol < 1) {
          throw new IllegalArgumentException(
              "Could not parse currency pair from '" + symbol + "'");
        }
        String base = symbol.substring(0, splitSymbol);
        String counter = symbol.substring(splitSymbol + 1);

        this.base = base;
        this.counter = counter;
        this.ticker = ticker;
    }
    
    @Override
    public String toString() {
      return base + BASE_COUNTER_SEP + counter + COUNTER_TICKER_SEP + ticker.getCode();
    }
    
    public String toCurrencyPairString() {
        return base + BASE_COUNTER_SEP + counter;
    }
    
    @Override
    public int hashCode() {

      final int prime = 31;
      int result = 1;
      result = prime * result + ((base == null) ? 0 : base.hashCode());
      result = prime * result + ((counter == null) ? 0 : counter.hashCode());
      result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Symbol other = (Symbol) obj;
        if (base == null) {
            if (other.base != null) {
                return false;
            }
        } else if (!base.equals(other.base)) {
            return false;
        }
        if (counter == null) {
            if (other.counter != null) {
                return false;
            }
        } else if (!counter.equals(other.counter)) {
            return false;
        }
        if (ticker == null) {
            if (other.ticker != null) {
                return false;
            }
        } else if (!ticker.equals(other.ticker)) {
            return false;
        }
        return true;
    }
}
