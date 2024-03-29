package com.bigmac.dao;

public class TickSchemaSql {
    
    public static final String CREATE_SCHEMA_SQL = "CREATE TABLE TICKS (TIMESTAMP BIGINT, SYMBOL VARCHAR(25), "
            + "LAST DOUBLE, VOLUME INTEGER, CHANGE DOUBLE)";
    
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS TICKS";
    
    public static final String SYMBOL_TICKER_IDX = "CREATE INDEX SYMBOL_IDX ON TICKS (SYMBOL)";
}
