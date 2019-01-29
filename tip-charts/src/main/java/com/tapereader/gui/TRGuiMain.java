package com.tapereader.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import com.google.inject.Inject;
import com.tapereader.clerk.Clerk;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.gui.chart.ChartUtils;
import com.tapereader.gui.marketdata.MarketDataPanel;
import com.tapereader.gui.marketdata.MarketDataTableModel;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Security;
import com.tapereader.model.Tip;
import com.tapereader.tip.SwingTip;

public class TRGuiMain implements Clerk {
    
    private final JFrame mainFrame;
    
    private JPanel chartWrapperPanel;
    
    private JComboBox<Tip> tipCombo;
    
    private JComboBox<TickerType> tickerCombo;
    
    private JComboBox<MarketType> marketCombo;
    
    private TRTable trTable;
    
    private static TRGuiMain trGui;
    
    private ChartPanel jfreeChartPanel;
    
    private MarketDataPanel marketDataPanel;
    
    private SwingTip swingTip;
    
    @Inject
    private TRGuiMain(SwingTip swingTip) {
        this.mainFrame = new JFrame("Bucketshop");
        this.swingTip = (SwingTip) swingTip;
        trGui = this;
    }
    
    private void createBaseGui() {
        getMainJFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel tipLbl = new JLabel("Tip");
        
        ActionListener comboListener = new TRComboBoxListener();
        List<Tip> tips = swingTip.getLookupClerk().getAllTips();
        tipCombo = new JComboBox<>(tips.toArray(new Tip[tips.size()]));
        tipCombo.addActionListener(comboListener);
        
        //jfreeChartPanel = initChartPanel();
        chartWrapperPanel = new JPanel();
        chartWrapperPanel.setLayout(new BorderLayout());
        
        JLabel shopLbl = new JLabel("Shop: ");
        List<TickerType> tickers = Arrays.asList(TickerType.values()).stream().collect(Collectors.toList());
        tickerCombo = new JComboBox<TickerType>(tickers.toArray(new TickerType[tickers.size()]));
        tickerCombo.addActionListener(comboListener);
        
        JLabel marketLbl = new JLabel("Market: ");
        List<MarketType> markets = Arrays.asList(MarketType.values()).stream().collect(Collectors.toList());
        marketCombo = new JComboBox<MarketType>(markets.toArray(new MarketType[markets.size()]));
        marketCombo.addActionListener(comboListener);
        
        List<Tick> ticks = swingTip.getMarketDataClerk().getCurrentTicks();
        trTable = new TRTable(new MarketDataTableModel(ticks));
        
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
        marketFeedPanel.setLayout(new BorderLayout());
        marketFeedPanel.add(marketDataComboPanel, BorderLayout.PAGE_START);
        marketFeedPanel.add(trTable, BorderLayout.CENTER);
        
        Container container = getContainer();
        container.setLayout(new BorderLayout());
        container.add(headerPanel, BorderLayout.PAGE_START);
        container.add(chartWrapperPanel, BorderLayout.CENTER);
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
        jfreeChartPanel = initChartPanel();
        chartWrapperPanel.add(jfreeChartPanel, BorderLayout.CENTER);
        chartWrapperPanel.validate();
    }
    
    private ChartPanel initChartPanel() {
        Security security = swingTip.getLookupClerk().findSecurity("BTC/USDT", TickerType.BINANCE);
        int lookback = swingTip.getConfiguration().getLookback();
        Instant start = Instant.now().minus(lookback, ChronoUnit.DAYS);
        
        //Building chart datasets
        JFreeChart chart = swingTip.buildJFreeChart(security, start, Duration.ofDays(1));
        
        // Set the chart
        jfreeChartPanel = ChartUtils.newJFreeAppFrame(chart);
        return jfreeChartPanel;
    }
    
    public void rebuildChart(String symbol) {
        SwingUtilities.invokeLater(() -> {
            Container container = getContainer();
            String[] split = symbol.split(":");
            container.remove(jfreeChartPanel);
            container.revalidate();
        });
    }
    
    public void setTip(String tip) {
        swingTip.setTipName(tip);
    }
    
    public void resetFilter(String market, String shop) {
        marketDataPanel.filterSecurities(market, shop);
    }

    @Override
    public void terminate() {
        
    }
    
    private static class TRComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    }

}
