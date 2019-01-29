package com.tapereader.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.tip.Tip;
import com.tapereader.tip.TipClerk;

public class TRGuiMain {
    
    private final JFrame mainFrame;
    
    private JComboBox<TipType> tipCombo;
    
    private JComboBox<TickerType> tickerCombo;
    
    private JComboBox<MarketType> marketCombo;
    
    private MarketDataTable trTable;
    
    private static TRGuiMain trGui;
    
    private ChartPanel jfreeChartPanel;
    
    private TipClerk tipClerk;
    
    public TRGuiMain(TipClerk tipClerk) {
        this.mainFrame = new JFrame("Bucketshop");
        this.tipClerk = tipClerk;
        trGui = this;
    }
    
    
    private void createBaseGui() {
        getMainJFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel tipLbl = new JLabel("Tip");
        
        ActionListener comboListener = new TRComboBoxListener();
        List<TipType> tips = Arrays.asList(TipType.values()).stream().collect(Collectors.toList());
        tipCombo = new JComboBox<>(tips.toArray(new TipType[tips.size()]));
        tipCombo.addActionListener(comboListener);
        
        jfreeChartPanel = initChartPanel();
        
        JLabel shopLbl = new JLabel("Shop: ");
        List<TickerType> tickers = Arrays.asList(TickerType.values()).stream().collect(Collectors.toList());
        tickerCombo = new JComboBox<TickerType>(tickers.toArray(new TickerType[tickers.size()]));
        tickerCombo.addActionListener(comboListener);
        
        JLabel marketLbl = new JLabel("Market: ");
        List<MarketType> markets = Arrays.asList(MarketType.values()).stream().collect(Collectors.toList());
        marketCombo = new JComboBox<MarketType>(markets.toArray(new MarketType[markets.size()]));
        marketCombo.addActionListener(comboListener);
        
        List<Tick> ticks = tipClerk.getMarketDataClerk().getCurrentTicks();
        JPanel marketDataPanel = new JPanel();
        marketDataPanel.setLayout(new BorderLayout(5, 5));
        trTable = new MarketDataTable(new MarketDataTableModel(ticks));
        JScrollPane scrollPane = new JScrollPane(trTable);
        marketDataPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel tipPanel = new JPanel();
        tipPanel.setLayout(new FlowLayout());
        tipPanel.add(tipLbl);
        tipPanel.add(tipCombo);
        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout(5,5));
        headerPanel.add(tipPanel, BorderLayout.LINE_END);
        
        JPanel marketDataComboPanel = new JPanel();
        marketDataComboPanel.setLayout(new FlowLayout());
        marketDataComboPanel.add(shopLbl);
        marketDataComboPanel.add(tickerCombo);
        marketDataComboPanel.add(marketLbl);
        marketDataComboPanel.add(marketCombo);
        
        JPanel marketFeedPanel = new JPanel();
        marketFeedPanel.setLayout(new BorderLayout(5, 5));
        marketFeedPanel.add(marketDataComboPanel, BorderLayout.PAGE_START);
        marketFeedPanel.add(marketDataPanel, BorderLayout.CENTER);
        
        Container container = getContainer();
        container.setLayout(new BorderLayout(5, 5));
        container.add(headerPanel, BorderLayout.PAGE_START);
        container.add(jfreeChartPanel, BorderLayout.CENTER);
        container.add(marketFeedPanel, BorderLayout.LINE_END);
    }
    
    public void runGui() {
        createBaseGui();
        getMainJFrame().setPreferredSize(new Dimension(1200, 600));
        getMainJFrame().pack();
        getMainJFrame().setVisible(true);
        UIUtils.centerFrameOnScreen(getMainJFrame());
        init();
    }
    
    public JFrame getMainJFrame() {
        return mainFrame;
    }
    
    private Container getContainer() {
        return mainFrame.getContentPane();
    }
    
    public static TRGuiMain getTrGui() {
        return trGui;
    }

    public void init() {
        
    }
    
    private ChartPanel initChartPanel() {
        Instant start = Instant.now().minus(100, ChronoUnit.DAYS);
        
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().build();
        List<Bar> bars = tipClerk.getHistoricalDataClerk()
                .getHistoricalBars("BTC/USDT", TickerType.BINANCE, start, Instant.now(), Duration.ofDays(1));
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        
        //Building chart datasets
        JFreeChart chart = tipClerk.getTip().buildJFreeChart("BTC/USDT", series);
        
        // Set the chart
        jfreeChartPanel = ChartUtils.newJFreeAppFrame(chart);
        return jfreeChartPanel;
    }
    
    public void setTip(String tip) {
        tipClerk.setTip(Tip.makeFactory(TipType.valueOf(tip)));
    }
    
    public void resetFilter(String market, String shop) {
        SwingUtilities.invokeLater(() -> {
            ((MarketDataTable) trTable).setFilter(market, shop);
        });
    }
    
    public void updateTable(List<Tick> ticks) {
        ((MarketDataTable) trTable).updateTicks(ticks);
    }
    
    private static class TRComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    }
    
}
