package com.bigmac.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigmac.domain.Symbol;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Bar;

public class BarDaoImpl extends AbstractDao implements BarDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(BarDaoImpl.class);

    public BarDaoImpl(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    public boolean save(Bar bar) throws Exception {
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = 
                    connection.prepareStatement("INSERT INTO BARS VALUES (?,?,?,?,?,?,?,?,?)")) {
                statement.setLong(1, bar.getTimestamp().toEpochMilli());
                statement.setString(2, bar.getSymbol().toString());
                statement.setDouble(3, bar.getOpen());
                statement.setDouble(4, bar.getHigh());
                statement.setDouble(5, bar.getLow());
                statement.setDouble(6, bar.getClose());
                statement.setInt(7, bar.getVolume());
                statement.setLong(8, bar.getDuration().toMillis());
              statement.execute();
              dbAuto.commit();
              return true;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public boolean update(Bar bar) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean save(Collection<Bar> bars) throws Exception {
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = 
                    connection.prepareStatement("INSERT INTO BARS VALUES (?,?,?,?,?,?,?,?,?)")) {
              for (Bar bar : bars) {
                  statement.setLong(1, bar.getTimestamp().toEpochMilli());
                  statement.setString(2, bar.getSymbol().toString());
                  statement.setDouble(3, bar.getOpen());
                  statement.setDouble(4, bar.getHigh());
                  statement.setDouble(5, bar.getLow());
                  statement.setDouble(6, bar.getClose());
                  statement.setInt(7, bar.getVolume());
                  statement.setLong(8, bar.getDuration().toMillis());
                  statement.addBatch();
              }
              statement.executeBatch();
              dbAuto.commit();
              return true;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public boolean delete(Bar bar) throws Exception {
        String sql = "DELETE FROM BARS WHERE SYMBOL = ? AND DURATION = ? AND TIMESTAMP = ?";
        LOGGER.debug("DELETE FROM BARS WHERE SYMBOL = {} AND DURATION = {} AND TIMESTAMP = {}", 
                bar.getSymbol(), bar.getDuration(), bar.getTimestamp());
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = 
                    connection.prepareStatement(sql)) {
                statement.setString(1, bar.getSymbol().toString());
                statement.setLong(2, bar.getDuration().toMillis());
                statement.setLong(3, bar.getTimestamp().toEpochMilli());
              int count = statement.executeUpdate();
              dbAuto.commit();
              LOGGER.debug("Deleted {} bars for {}", count, bar.getSymbol());
              return true;
        } catch (SQLException ex) {
            LOGGER.error("Failed to delete last bar..", ex);
            throw ex;
        }
    }
    
    private Bar createBar(ResultSet resultSet) throws SQLException {
        return new Bar(Instant.ofEpochMilli(resultSet.getLong("TIMESTAMP")), 
            new Symbol(resultSet.getString("SYMBOL"),
            TickerType.valueOf(resultSet.getString("TICKER"))),
            resultSet.getDouble("OPEN"),
            resultSet.getDouble("HIGH"),
            resultSet.getDouble("LOW"),
            resultSet.getDouble("CLOSE"),
            resultSet.getInt("VOLUME"),
            Duration.ofMillis(resultSet.getLong("DURATION")));
    }

    @Override
    public List<Bar> loadAll() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
