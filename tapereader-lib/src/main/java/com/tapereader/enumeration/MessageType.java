package com.tapereader.enumeration;

import java.time.Duration;
import java.util.function.Function;

import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketData;
import com.tapereader.marketdata.Tick;

public enum MessageType implements Function<String[], MarketData> {

    TICK {
        @Override
        public MarketData apply(String[] t) {
            return new Tick(Long.parseLong(t[1]), t[2], Double.parseDouble(t[3]), Integer.parseInt(t[4]));
        }
    },
    BAR {
        @Override
        public MarketData apply(String[] b) {
            return new Bar(Long.parseLong(b[1]), b[2], Duration.parse(b[3]), Double.parseDouble(b[4]),
                    Double.parseDouble(b[5]), Double.parseDouble(b[6]), Double.parseDouble(b[7]),
                    Integer.parseInt(b[8]));
        }
    };
    
    public static MessageType valueOf(String[] msgArr) {
        return MessageType.valueOf(msgArr[0]);
    }

}
