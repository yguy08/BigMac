package com.bigmac.gui;

import java.util.function.Function;

import com.bigmac.marketdata.Tick;

public class MarketDataTableMapper implements TableModelMapper {

    public String[] columnNames = {"Symbol","Last","Volume", "%"};
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    @Override
    public Object getValueAt(Object row, int col) {
        Tick tick = (Tick) row;
        try {
            switch(col) {
            case 0:
                return tick.getSymbol().toCurrencyPairString();
            case 1:
                return DECIMALFUNC.apply(tick);
            case 2:
                return tick.getVolume();
            case 3:
                return tick.getPriceChangePercent();
            default:
                return "ERROR";
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "ERROR";
    }
    
    private Function<Tick, String> DECIMALFUNC = t -> {
        return t.getSymbol().toCurrencyPairString().endsWith("USDT") ? String.format("%.2f", t.getLast()) :
            String.format("%.8f", t.getLast());
    };

    public Class<?> getColumnClass(int c) {
        try {
            switch(c) {
            case 0:
                return String.class;
            case 1:
                return Number.class;
            case 2:
                return Integer.class;
            case 3:
                return Double.class;
            default:
                return String.class;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return String.class;
    }

}
