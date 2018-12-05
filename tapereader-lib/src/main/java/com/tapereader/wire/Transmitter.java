package com.tapereader.wire;

import com.tapereader.clerk.Clerk;

public interface Transmitter extends Clerk {

    void transmit(Object event);

}
