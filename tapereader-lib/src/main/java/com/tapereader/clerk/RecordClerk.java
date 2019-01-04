package com.tapereader.clerk;

import java.util.List;

import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Line;
import com.tapereader.model.Security;

public interface RecordClerk extends Clerk {
    
    void updateBars(Security security, List<Bar> bars);
    
    void persist(Object object);
    
    void saveTicks(List<Tick> ticks);
}
