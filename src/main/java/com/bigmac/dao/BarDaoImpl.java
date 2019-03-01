package com.bigmac.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
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

    @Override
    public List<Bar> getAllBySymbolTickerAndDuration(String symbol, String ticker, long start, long end, long duration) throws Exception {
        String sql = "SELECT * FROM BARS WHERE SYMBOL = ? AND "
                + "TICKER = ? AND TIMESTAMP BETWEEN ? AND ? AND DURATION = ?";
        LOGGER.debug("SELECT * FROM BARS WHERE SYMBOL = {} AND "
                + "TICKER = {} AND TIMESTAMP BETWEEN {} AND {} AND DURATION = {}", symbol, ticker, start, end, duration);
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);){
          statement.setString(1, symbol);
          statement.setString(2, ticker);
          statement.setLong(3, start);
          statement.setLong(4, end + duration);
          statement.setLong(5, duration);
          List<Bar> bars = new ArrayList<>();
          try (ResultSet resultSet = statement.executeQuery()) {
              while (resultSet.next()) {
                  bars.add(createBar(resultSet));
              }
          }
          return bars;
        } catch (SQLException e) {
          throw new Exception(e.getMessage(), e);
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
    public void deleteLastBarBySymbolTickerAndDuration(String symbol, String ticker, long duration) throws Exception {
        String sql = "SELECT * FROM BARS WHERE SYMBOL = ? AND TICKER = ? AND DURATION = ? ORDER BY TIMESTAMP DESC LIMIT 1";
        LOGGER.debug("SELECT * FROM BARS WHERE SYMBOL = ? AND TICKER = ? AND DURATION = ? ORDER BY TIMESTAMP DESC LIMIT 1", 
                symbol, ticker, duration);
        try (Connection connection = getConnection();
                PreparedStatement statement = 
                    connection.prepareStatement(sql)) {
                statement.setString(1, symbol);
                statement.setString(2, ticker);
                statement.setLong(3, duration);
                Bar bar = null;
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        bar = createBar(resultSet);
                    }
                }
                if (bar != null) {
                    LOGGER.debug("Last Bar found {}", bar);
                    delete(bar);
                    LOGGER.debug("Last Bar deleted. Successfully.");
                } else {
                    LOGGER.debug("Last Bar was not found for {}", symbol);
                }
        } catch (SQLException ex) {
            LOGGER.error("Failed to delete last bar..", ex);
            throw ex;
        }
    }

    @Override
    public Bar findBySymbolTickerDurationAndTimestamp(String symbol, String ticker, long duration, long timestamp)
            throws Exception {
        String sql = "SELECT * FROM BARS WHERE SYMBOL = ? AND TICKER = ? AND DURATION = ? AND TIMESTAMP = ? LIMIT 1";
        LOGGER.debug("SELECT * FROM BARS WHERE SYMBOL = ? AND TICKER = ? AND DURATION = ? AND TIMESTAMP = {} LIMIT 1", 
                symbol, ticker, duration, timestamp);
        try (Connection connection = getConnection();
                PreparedStatement statement = 
                    connection.prepareStatement(sql)) {
                statement.setString(1, symbol);
                statement.setString(2, ticker);
                statement.setLong(3, duration);
                statement.setLong(4, timestamp);
                Bar bar = null;
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        bar = createBar(resultSet);
                    }
                }
                return bar;
        } catch (SQLException ex) {
            LOGGER.error("Failed to delete last bar..", ex);
            throw ex;
        }
    }

}
