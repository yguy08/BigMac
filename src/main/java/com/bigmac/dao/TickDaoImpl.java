package com.bigmac.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigmac.domain.Symbol;
import com.bigmac.enumeration.TickerType;
import com.bigmac.marketdata.Tick;

public class TickDaoImpl extends AbstractDao implements TickDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickDaoImpl.class);

    public TickDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean save(Tick tick) throws Exception {
        String sql = "INSERT INTO TICKS VALUES (?,?,?,?,?,?)";
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, tick.getTimestamp().toEpochMilli());
            statement.setString(2, tick.getSymbol().toString());
            statement.setDouble(3, tick.getLast());
            statement.setInt(4, tick.getVolume());
            statement.setDouble(5, tick.getPriceChangePercent());
            statement.execute();
            dbAuto.commit();
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
    public boolean save(Collection<Tick> ticks) throws Exception {
        String sql = "INSERT INTO TICKS VALUES (?,?,?,?,?)";
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Tick tick : ticks) {
                statement.setLong(1, tick.getTimestamp().toEpochMilli());
                statement.setString(2, tick.getSymbol().toString());
                statement.setDouble(3, tick.getLast());
                statement.setInt(4, tick.getVolume());
                statement.setDouble(5, tick.getPriceChangePercent());
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
    public boolean delete(Tick tick) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Tick> getAllByTicker(String ticker) throws Exception {
        String sql = "SELECT * FROM TICKS WHERE SYMBOL LIKE ?";
        try (Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, "%:" + ticker);
            List<Tick> ticks = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ticks.add(createTick(resultSet));
                }
            }
            return ticks;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    @Override
    public Tick findBySymbol(String symbol) throws Exception {
        String sql = "SELECT * FROM TICKS WHERE SYMBOL = ?";
        try (Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, new Symbol(symbol).toString());
            Tick tick = null;
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tick = createTick(resultSet);
                }
            }
            return tick;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
    
    private Tick createTick(ResultSet resultSet) throws SQLException {
        return new Tick(resultSet.getLong("TIMESTAMP"), resultSet.getString("SYMBOL"),
                resultSet.getDouble("LAST"), resultSet.getInt("VOLUME"), 
                resultSet.getDouble("CHANGE"));
    }

    @Override
    public void deleteAll() throws Exception {
        String sql = "DELETE FROM TICKS";
        try (Connection connection = getConnection();
                DbAutoTransaction dbAuto = new DbAutoTransaction(connection, false);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            dbAuto.commit();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public List<Tick> loadAll() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
