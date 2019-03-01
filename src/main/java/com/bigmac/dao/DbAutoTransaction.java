package com.bigmac.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DbAutoTransaction implements AutoCloseable {
    
    private Connection con;
    private boolean committed;
    private boolean originalAutoCommit;

    public DbAutoTransaction(Connection con, boolean autoCommit) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("con must not be null");
        }
        this.con = con;
        originalAutoCommit = con.getAutoCommit();
        con.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        con.commit();
        committed = true;
    }

    @Override
    public void close() throws SQLException {
        if (!committed) {
            con.rollback();
        }
        con.setAutoCommit(originalAutoCommit);
    }
}
