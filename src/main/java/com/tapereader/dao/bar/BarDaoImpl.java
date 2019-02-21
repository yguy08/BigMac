package com.tapereader.dao.bar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;

public class BarDaoImpl implements BarDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BarDaoImpl.class);

    private final DataSource dataSource;
    
    public BarDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
      }

    @Override
    public Stream<Bar> getAll() throws Exception {
        Connection connection;
        try {
          connection = getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT * FROM BARS"); // NOSONAR
          ResultSet resultSet = statement.executeQuery(); // NOSONAR
          return StreamSupport.stream(new Spliterators.AbstractSpliterator<Bar>(Long.MAX_VALUE, 
              Spliterator.ORDERED) {

            @Override
            public boolean tryAdvance(Consumer<? super Bar> action) {
              try {
                if (!resultSet.next()) {
                  return false;
                }
                action.accept(createBar(resultSet));
                return true;
              } catch (SQLException e) {
                throw new RuntimeException(e); // NOSONAR
              }
            }
          }, false).onClose(() -> mutedClose(connection, statement, resultSet));
        } catch (SQLException e) {
          throw new Exception(e.getMessage(), e);
        }
    }

    @Override
    public boolean add(Bar bar) throws Exception {
        try (Connection connection = getConnection();
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
    public boolean add(Collection<Bar> bars) throws Exception {
        try (Connection connection = getConnection();
                PreparedStatement statement = 
                    connection.prepareStatement("INSERT INTO BARS VALUES (?,?,?,?,?,?,?,?,?)")) {
              connection.setAutoCommit(false);
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
              connection.commit();
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
    public Stream<Bar> getAllBySymbolTickerAndDuration(String symbol, String ticker, long start, long end, long duration) throws Exception {
        Connection connection;
        try {
          connection = getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT * FROM BARS WHERE SYMBOL = ? AND "
                  + "TICKER = ? AND TIMESTAMP BETWEEN ? AND ? AND DURATION = ?"); // NOSONAR
          statement.setString(1, symbol);
          statement.setString(2, ticker);
          statement.setLong(3, start - duration);
          statement.setLong(4, end + duration);
          statement.setLong(5, duration);
          ResultSet resultSet = statement.executeQuery(); // NOSONAR
          return StreamSupport.stream(new Spliterators.AbstractSpliterator<Bar>(Long.MAX_VALUE, 
              Spliterator.ORDERED) {

            @Override
            public boolean tryAdvance(Consumer<? super Bar> action) {
              try {
                if (!resultSet.next()) {
                  return false;
                }
                action.accept(createBar(resultSet));
                return true;
              } catch (SQLException e) {
                throw new RuntimeException(e); // NOSONAR
              }
            }
          }, false).onClose(() -> mutedClose(connection, statement, resultSet));
        } catch (SQLException e) {
          throw new Exception(e.getMessage(), e);
        }
    }
    
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
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
    
    private void mutedClose(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
          resultSet.close();
          statement.close();
          connection.close();
        } catch (SQLException e) {
          LOGGER.info("Exception thrown " + e.getMessage());
        }
      }

}
