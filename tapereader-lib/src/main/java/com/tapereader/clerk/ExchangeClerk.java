package com.tapereader.clerk;

import java.util.List;

import com.tapereader.marketdata.Tick;

public interface ExchangeClerk extends Clerk {

    List<Tick> getCurrentTicks();

}
