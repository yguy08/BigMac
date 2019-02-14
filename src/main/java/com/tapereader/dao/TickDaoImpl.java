package com.tapereader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Tick;

public class TickDaoImpl implements TickDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TickDaoImpl.class);

    private final DataSource dataSource;
    
    public TickDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
      }

    @Override
    public Stream<Tick> getAll() throws Exception {
        Connection connection;
        try {
          connection = getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKS"); // NOSONAR
          ResultSet resultSet = statement.executeQuery(); // NOSONAR
          return StreamSupport.stream(new Spliterators.AbstractSpliterator<Tick>(Long.MAX_VALUE, 
              Spliterator.ORDERED) {

            @Override
            public boolean tryAdvance(Consumer<? super Tick> action) {
              try {
                if (!resultSet.next()) {
                  return false;
                }
                action.accept(createTick(resultSet));
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
    public boolean add(Tick tick) throws Exception {
        try (Connection connection = getConnection();
                PreparedStatement statement = 
                    connection.prepareStatement("INSERT INTO TICKS VALUES (?,?,?,?,?,?)")) {
              statement.setLong(1, tick.getTimestamp());
              statement.setString(2, tick.getSymbol());
              statement.setString(3, tick.getTicker().toString());
              statement.setDouble(4, tick.getLast());
              statement.setInt(5, tick.getVolume());
              statement.setDouble(6, tick.getPriceChangePercent());
              statement.execute();
              return true;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public boolean update(Tick tick) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean add(Collection<Tick> ticks) throws Exception {
        try (Connection connection = getConnection();
                PreparedStatement statement = 
                    connection.prepareStatement("INSERT INTO TICKS VALUES (?,?,?,?,?,?)")) {
              connection.setAutoCommit(false);
              for (Tick tick : ticks) {
                  statement.setLong(1, tick.getTimestamp());
                  statement.setString(2, tick.getSymbol());
                  statement.setString(3, tick.getTicker().toString());
                  statement.setDouble(4, tick.getLast());
                  statement.setInt(5, tick.getVolume());
                  statement.setDouble(6, tick.getPriceChangePercent());
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
    public boolean delete(Tick tick) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Stream<Tick> getAllByTicker(String ticker) throws Exception {
        Connection connection;
        try {
          connection = getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT * FROM TICKS WHERE TICKER = ?"); // NOSONAR
          statement.setString(1, ticker);
          ResultSet resultSet = statement.executeQuery(); // NOSONAR
          return StreamSupport.stream(new Spliterators.AbstractSpliterator<Tick>(Long.MAX_VALUE, 
              Spliterator.ORDERED) {

            @Override
            public boolean tryAdvance(Consumer<? super Tick> action) {
              try {
                if (!resultSet.next()) {
                  return false;
                }
                action.accept(createTick(resultSet));
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
    
    private Tick createTick(ResultSet resultSet) throws SQLException {
        return new Tick(resultSet.getLong("TIMESTAMP"), 
            resultSet.getString("SYMBOL"),
            TickerType.valueOf(resultSet.getString("TICKER")),
            resultSet.getDouble("LAST"),
            resultSet.getInt("VOLUME"),
            resultSet.getDouble("CHANGE"));
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
