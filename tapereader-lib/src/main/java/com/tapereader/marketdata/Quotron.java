package com.tapereader.marketdata;

import com.tapereader.clerk.Clerk;
import com.tapereader.model.Security;

/**
 * The Quotron consists of a storage unit that records data from the ticker line
 * and can perform retrievals on the data.
 * 
 * @author wendre01
 *
 */
public interface Quotron extends Clerk {

    void startRecording();

    Tick getLastMarketEvent(Security security);

}
