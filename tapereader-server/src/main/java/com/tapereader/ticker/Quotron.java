package com.tapereader.ticker;

import com.tapereader.clerk.Clerk;

/**
 * The Quotron consists of a storage unit that records data from the ticker line
 * and can perform retrievals on the data.
 * 
 * @author wendre01
 *
 */
public interface Quotron extends Clerk {

    void startRecording();

}
