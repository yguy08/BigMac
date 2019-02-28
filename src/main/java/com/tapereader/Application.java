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
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.adapter.ExchangeAdapter;
import com.tapereader.adapter.bnc.BinanceExchangeAdapter;
import com.tapereader.adapter.cpro.CProExchangeAdapter;
import com.tapereader.adapter.polo.PoloniexExchangeAdapter;
import com.tapereader.chart.ChartManager;
import com.tapereader.chart.strategy.BuyHigh;
import com.tapereader.chart.strategy.ChartStrategy;
import com.tapereader.config.Config;
import com.tapereader.db.dao.bar.BarDao;
import com.tapereader.db.dao.bar.BarDaoImpl;
import com.tapereader.db.dao.bar.BarSchemaSql;
import com.tapereader.db.dao.tick.TickDao;
import com.tapereader.db.dao.tick.TickDaoImpl;
import com.tapereader.db.dao.tick.TickSchemaSql;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.gui.TRGuiMain;
import com.tapereader.gui.controller.TipClerk;
import com.tapereader.marketdata.MarketDataClerk;
import com.tapereader.marketdata.MarketDataClerkImpl;
import com.tapereader.marketdata.cache.MarketDataCacheClerk;
import com.tapereader.marketdata.cache.MarketDataCacheClerkImpl;
import com.tapereader.marketdata.historical.HistoricalDataClerk;
import com.tapereader.marketdata.historical.HistoricalDataClerkImpl;

public class Application {

    private final String propertiesFile;

    private final static String DB_URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE";

    public Application(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    private void run() {
        /* Load Properties File */
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(propertiesFile);
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties file.");
        }

        ExchangeAdapter bncAdapter = new BinanceExchangeAdapter();

        ExchangeAdapter poloAdapter = new PoloniexExchangeAdapter();

        ExchangeAdapter cproAdapter = new CProExchangeAdapter();

        Map<String, ExchangeAdapter> adapterMap = new HashMap<>();
        adapterMap.put(TickerType.BINANCE.toString(), bncAdapter);
        adapterMap.put(TickerType.POLONIEX.toString(), poloAdapter);
        adapterMap.put(TickerType.CPRO.toString(), cproAdapter);

        final DataSource dataSource = createDataSource();
        try {
            boolean createSchema = Boolean.parseBoolean(properties.getProperty("createSchema"));
            if (createSchema) {
                createSchema(dataSource);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TickDao tickDao = new TickDaoImpl(dataSource);
        BarDao barDao = new BarDaoImpl(dataSource);

        MarketDataClerk marketDataClerk = new MarketDataClerkImpl(adapterMap, tickDao);
        HistoricalDataClerk historicalDataClerk = new HistoricalDataClerkImpl(adapterMap, barDao);
        MarketDataCacheClerk cacheClerk = new MarketDataCacheClerkImpl(tickDao, barDao);

        Config config = new Config();
        config.setBarSize(Duration.ofDays(1));
        config.setDefaultSymbol("BTC/USDT");
        config.setDefaultTip(TipType.BUY_HIGH);
        config.setLookback(150);
        config.setTickerType(TickerType.BINANCE);
        config.setMarketType(MarketType.BTC);
        config.setOutOfDateSeconds(300);

        TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName(config.getDefaultSymbol()).build();
        ChartStrategy strategy = new BuyHigh(series);
        TipClerk tipClerk = new TipClerk(config, marketDataClerk, historicalDataClerk, 
                cacheClerk, new ChartManager(strategy));

        TRGuiMain app = new TRGuiMain(tipClerk);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.createAndShowGui();
            }
        });
    }

    private static void createSchema(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            // TICK
            statement.execute(TickSchemaSql.DROP_TABLE_SQL);
            statement.execute(TickSchemaSql.CREATE_SCHEMA_SQL);
            statement.execute(TickSchemaSql.SYMBOL_TICKER_IDX);
            // BAR
            statement.execute(BarSchemaSql.DROP_TABLE_SQL);
            statement.execute(BarSchemaSql.CREATE_SCHEMA_SQL);
            statement.execute(BarSchemaSql.BAR_IDX);
        }
    }

    private static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        return dataSource;
    }

    public static void main(String[] args) {
        new Application("application.properties").run();
    }

}
