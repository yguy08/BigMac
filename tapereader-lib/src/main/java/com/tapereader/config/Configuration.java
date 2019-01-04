package com.tapereader.config;

import java.time.Duration;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Configuration {

    @Inject(optional = true)
    @Named("lookback")
    private String lookback;

    @Inject(optional = true)
    @Named("barsize")
    private String barSize;
    
    @Inject(optional = true)
    @Named("ignorebardays")
    private String ignoreBarDays;
    
    public int getLookback() {
        try {
            return Integer.parseInt(lookback);
        } catch (Exception e) {
            return 300;
        }
    }

    public Duration getBarSize() {
        try {
            return Duration.parse(barSize);
        } catch (Exception e) {
            return Duration.ofDays(1);
        }
    }
    
    public int getIgnoreBarDays() {
        try {
            return Integer.parseInt(ignoreBarDays);
        } catch (Exception e) {
            return 0;
        }
    }

}
