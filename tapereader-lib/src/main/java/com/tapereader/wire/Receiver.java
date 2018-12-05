package com.tapereader.wire;

import com.tapereader.clerk.Clerk;

public interface Receiver extends Clerk {

    void receive(Object object);

}
