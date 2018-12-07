package com.tapereader.clerk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Security;

public interface MarketDataClerk extends Clerk {

    List<Tick> getCurrentTicks();

}
