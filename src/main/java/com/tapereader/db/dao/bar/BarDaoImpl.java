package com.tapereader.db.dao.bar;

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

import com.tapereader.db.dao.AbstractDao;
import com.tapereader.db.util.DbAutoTransaction;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;

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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Bar> getAllBySymbolTickerAndDuration(String symbol, String ticker, long start, long end, long duration) throws Exception {
        String sql = "SELECT * FROM BARS WHERE SYMBOL = ? AND "
                + "TICKER = ? AND TIMESTAMP BETWEEN ? AND ? AND DURATION = ?";
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);){
          statement.setString(1, symbol);
          statement.setString(2, ticker);
          statement.setLong(3, start - duration);
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
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = 
                    connection.prepareStatement("DELETE FROM BARS WHERE SYMBOL = ? AND TICKER = ? AND DURATION = ?")) {
                statement.setString(1, symbol);
                statement.setString(2, ticker);
                statement.setLong(3, duration);
              statement.execute();
              dbAuto.commit();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public List<Bar> getAllBySymbolTicker(String symbol, String ticker, long start, long end) throws Exception {
        String sql = "SELECT * FROM BARS WHERE SYMBOL = ? AND "
                + "TICKER = ? AND TIMESTAMP >= ? AND TIMESTAMP <= ?";
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);){
          statement.setString(1, symbol);
          statement.setString(2, ticker);
          statement.setLong(3, start);
          statement.setLong(4, end);
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

}
