package com.bigmac;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.bigmac.adapter.BinanceExchangeAdapter;
import com.bigmac.adapter.CProExchangeAdapter;
import com.bigmac.adapter.ExchangeAdapter;
import com.bigmac.adapter.PoloniexExchangeAdapter;
import com.bigmac.chart.strategy.BuyHigh;
import com.bigmac.chart.strategy.ChartStrategy;
import com.bigmac.config.Config;
import com.bigmac.dao.BarDao;
import com.bigmac.dao.BarDaoImpl;
import com.bigmac.dao.BarSchemaSql;
import com.bigmac.dao.TickDao;
import com.bigmac.dao.TickDaoImpl;
import com.bigmac.dao.TickSchemaSql;
import com.bigmac.enumeration.LookbackPeriod;
import com.bigmac.enumeration.MarketType;
import com.bigmac.enumeration.TickerType;
import com.bigmac.enumeration.TipType;
import com.bigmac.gui.TRGuiMain;
import com.bigmac.gui.controller.TipClerk;
import com.bigmac.marketdata.MarketDataClerk;
import com.bigmac.marketdata.MarketDataClerkImpl;
import com.bigmac.marketdata.cache.MarketDataCacheClerk;
import com.bigmac.marketdata.cache.MarketDataCacheClerkImpl;
import com.bigmac.marketdata.historical.HistoricalDataClerk;
import com.bigmac.marketdata.historical.HistoricalDataClerkImpl;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private final String propertiesFile;

    private final static String DB_URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE";

    private static final String BANNER_FILE = "banner.txt";

    private static final String SEP = File.separator;

    public Application(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    private void run() {
        displayBanner();
        /* Load Properties File */
        String path = "." + SEP + "src" + SEP + "main" + SEP + "resources" + SEP + propertiesFile;
        InputStream input = null;
        Properties properties = new Properties();
        try {
            input = new FileInputStream(path);
            properties.load(input);
        } catch (IOException e) {
            LOGGER.warn("Unable to load properties file from {}. Using defaults.", path);
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
        config.setLookback(LookbackPeriod.M3.getPeriod());
        config.setTickerType(TickerType.BINANCE);
        config.setMarketType(MarketType.BTC);

        TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName(config.getDefaultSymbol()).build();
        ChartStrategy strategy = new BuyHigh(series);
        TipClerk tipClerk = new TipClerk(config, marketDataClerk, historicalDataClerk, cacheClerk, strategy);

        TRGuiMain app = new TRGuiMain(tipClerk);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                app.createAndShowGui();
            }
        });
    }
    
    private static void displayBanner() {
        try {
            String path = "." + SEP + "src" + SEP + "main" + SEP + "resources" + SEP + BANNER_FILE;
            File file = new File(path);
            String banner = Files.readAllLines(Paths.get(file.getPath())).stream().map(s -> s.toString())
                    .collect(Collectors.joining("\n"));
            System.out.println("\n\n" + banner);
        } catch (Exception e) {
            LOGGER.debug("Unable to load banner.", e);
        }
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

    public static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        return dataSource;
    }

    public static void main(String[] args) {
        String properties = null;
        if (args.length > 0) {
            properties = args[0];
        } else {
            properties = "application.properties";
        }
        new Application(properties).run();
    }

}
