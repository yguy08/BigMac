package com.tapereader.enumeration;

public enum LookbackPeriod {
    
    D1(1), W1(7), M1(30), M3(90), M6(180), Y1(365), Y2(365 * 2), MAX(365 * 5);
    
    private int period;
    
    LookbackPeriod(int period) {
        this.period = period;
    }
    
    public int getPeriod() {
        return period;
    }
}
