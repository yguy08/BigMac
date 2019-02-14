package com.tapereader.dao;

public class TickSchemaSql {
    
    public static final String CREATE_SCHEMA_SQL = "CREATE TABLE TICKS (TIMESTAMP BIGINT, SYMBOL VARCHAR(25), "
            + "TICKER VARCHAR(25), LAST DOUBLE, VOLUME INTEGER, CHANGE DOUBLE)";
    
}
