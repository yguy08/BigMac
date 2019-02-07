package com.tapereader.enumeration;

public enum TipType {
    
    BUY_HIGH("Buy High");
    
    private String displayName;
    
    TipType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return getDisplayName();
    }
}
