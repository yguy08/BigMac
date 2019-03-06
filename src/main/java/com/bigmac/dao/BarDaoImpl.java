package com.bigmac.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                statement.setLong(1, bar.getTimestamp());
                statement.setString(2, bar.getSymbol());
                statement.setString(3, bar.getTicker().toString());
                statement.setDouble(4, bar.getOpen());
                statement.setDouble(5, bar.getHigh());
                statement.setDouble(6, bar.getLow());
                statement.setDouble(7, bar.getClose());
                statement.setInt(8, bar.getVolume());
                statement.setLong(9, bar.getDuration().toMillis());
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
                  statement.setLong(1, bar.getTimestamp());
                  statement.setString(2, bar.getSymbol());
                  statement.setString(3, bar.getTicker().toString());
                  statement.setDouble(4, bar.getOpen());
                  statement.setDouble(5, bar.getHigh());
                  statement.setDouble(6, bar.getLow());
                  statement.setDouble(7, bar.getClose());
                  statement.setInt(8, bar.getVolume());
                  statement.setLong(9, bar.getDuration().toMillis());
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
        String sql = "DELETE FROM BARS WHERE SYMBOL = ? AND TICKER = ? AND DURATION = ? AND TIMESTAMP = ?";
        LOGGER.debug("DELETE FROM BARS WHERE SYMBOL = {} AND TICKER = {} AND DURATION = {} AND TIMESTAMP = {}", 
                bar.getSymbol(), bar.getTicker(), bar.getDuration(), bar.getTimestamp());
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = 
                    connection.prepareStatement(sql)) {
                statement.setString(1, bar.getSymbol());
                statement.setString(2, bar.getTicker().toString());
                statement.setLong(3, bar.getDuration().toMillis());
                statement.setLong(4, bar.getTimestamp());
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
        return new Bar(resultSet.getLong("TIMESTAMP"), 
            resultSet.getString("SYMBOL"),
            TickerType.valueOf(resultSet.getString("TICKER")),
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
