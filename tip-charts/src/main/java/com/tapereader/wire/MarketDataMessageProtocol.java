package com.tapereader.wire;

import com.tapereader.enumeration.MessageType;
import com.tapereader.marketdata.MarketData;
import com.tapereader.marketdata.Tick;

public class MarketDataMessageProtocol implements MessageProtocol {

    @Override
    public MarketData handleProtocolMessage(String messageText) {
        String[] msgArr = messageText.split(" ");
        try {
            return MessageType.valueOf(msgArr).apply(msgArr);
        } catch (Exception e) {
            System.out.println(e);
        }
        return new Tick();
    }

}
