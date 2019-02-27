package com.tapereader.engine;

import com.espertech.esper.client.EPStatement;

public interface Engine {
    
    void sendEvent(Object event);

    EPStatement createEPL(String eplStatement, String statementName);
    
    public void loadStatements(String source, Object subscriber);

    public void processAnnotations(EPStatement statement, Object obj) throws Exception;

}
