package com.bigmac.enumeration;

public enum TipType {
    
    BUY_HIGH("Buy High"),
    BUY_LOW("Buy Low"),
    DOUBLE_U("Double (You)");
    
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
    
    public static TipType findByDisplayName(String displayName) {
        for (TipType type : TipType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return BUY_HIGH;
    }
}
