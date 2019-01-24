package com.tapereader.clerk;

/**
 * The Quotron consists of a storage unit that records data from the ticker line
 * and can perform retrievals on the data.
 * 
 * @author wendre01
 *
 */
public interface MarketDataStreamingClerk extends Clerk {

    void startRecording();

}
