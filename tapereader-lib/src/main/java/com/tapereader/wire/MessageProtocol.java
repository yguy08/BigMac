package com.tapereader.wire;

public interface MessageProtocol {
    Object handleProtocolMessage(String messageText);
}
