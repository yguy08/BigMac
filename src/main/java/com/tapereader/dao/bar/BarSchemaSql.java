package com.tapereader.dao.bar;

public class BarSchemaSql {
    
    public static final String CREATE_SCHEMA_SQL = "CREATE TABLE BARS (TIMESTAMP BIGINT, SYMBOL VARCHAR(25), "
            + "TICKER VARCHAR(25), OPEN DOUBLE, HIGH DOUBLE, LOW DOUBLE, CLOSE DOUBLE,"
            + "VOLUME INTEGER, DURATION BIGINT)";
    
    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS BARS";
    
}
