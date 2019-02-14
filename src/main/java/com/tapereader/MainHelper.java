package com.tapereader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.cpro.CProExchangeAdapter;
import com.tapereader.adapter.polo.PoloniexExchangeAdapter;
import com.tapereader.chart.ChartManager;
import com.tapereader.chart.TipClerk;
import com.tapereader.chart.strategy.buyhigh.BuyHigh;
import com.tapereader.config.Config;
import com.tapereader.dao.TickDao;
import com.tapereader.dao.TickDaoImpl;
import com.tapereader.dao.TickSchemaSql;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.gui.TRGuiMain;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.MarketDataClerkImpl;
import com.tapereader.marketdata.historical.HistoricalDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerkImpl;

public class MainHelper {
    
    private final String[] args;
    
    private final String propertiesFileName;
    
    private final ExchangeAdapter bncAdapter;
    
    private final static String DB_URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE";
    
    public MainHelper(String[] args, String propertiesFileName, ExchangeAdapter bncAdapter) {
        this.args = args;
        this.propertiesFileName = propertiesFileName;
        this.bncAdapter = bncAdapter;
    }
    
    public void run() {
        /* Load Properties File */
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(propertiesFileName);
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties file.");
        }
        
        bncAdapter.init();
        
        ExchangeAdapter poloAdapter = new PoloniexExchangeAdapter();
        poloAdapter.init();
        
        ExchangeAdapter cproAdapter = new CProExchangeAdapter();
        cproAdapter.init();
        
        Map<String, ExchangeAdapter> adapterMap = new HashMap<>();
        adapterMap.put(TickerType.BINANCE.toString(), bncAdapter);
        adapterMap.put(TickerType.POLONIEX.toString(), poloAdapter);
        adapterMap.put(TickerType.CPRO.toString(), cproAdapter);
        
        final DataSource dataSource = createDataSource();
        try {
            createSchema(dataSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TickDao tickDao = new TickDaoImpl(dataSource);
        
        MarketDataClerk marketDataClerk = new MarketDataClerkImpl(adapterMap, tickDao);
        HistoricalDataClerk historicalDataClerk = new HistoricalDataClerkImpl(adapterMap);
        
        Config config = new Config();
        config.setBarSize(Duration.ofDays(1));
        config.setDefaultSymbol("BTC/USDT");
        config.setDefaultTip(TipType.BUY_HIGH);
        config.setLookback(150);
        config.setTickerType(TickerType.BINANCE);
        config.setMarketType(MarketType.BTC);
        
        TipClerk tipClerk = new TipClerk(config, marketDataClerk, historicalDataClerk, new ChartManager(), new BuyHigh());
        
        TRGuiMain app = new TRGuiMain(tipClerk);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.createAndShowGui();
            }
        });
    }
    
    private static void createSchema(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE TICKS");
          statement.execute(TickSchemaSql.CREATE_SCHEMA_SQL);
        }
      }
    
    private static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        return dataSource;
      }

}
