package com.tapereader.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tapereader.bus.TrRoleLogic;
import com.tapereader.clerk.Clerk;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.gui.chart.ChartUtils;
import com.tapereader.gui.marketdata.MarketDataPanel;
import com.tapereader.gui.order.OrderPanel;
import com.tapereader.gui.position.PositionPanel;
import com.tapereader.gui.transaction.TransactionPanel;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Security;
import com.tapereader.model.Tip;
import com.tapereader.tip.TapeReader;

public class TapeReaderGuiMain implements Clerk {
    
    private JFrame frame;
    
    private static TapeReaderGuiMain trGui;
    
    private ChartPanel chartPanel;
    
    private Container container;
    
    private JPanel rightPanel;
    
    private JPanel leftPanel;
    
    @Inject
    @Named("apprefresh")
    private String INTERVAL;
    
    private Timer timer;
    
    private MarketDataPanel marketDataPanel;
    
    private TrRoleLogic trRoleLogic;
    
    private JComboBox<BucketShop> shopCombo;
    
    private JComboBox<Tip> tipCombo;
    
    @Inject
    private TapeReaderGuiMain(TapeReader tapeReader) {
        this.trRoleLogic = (TrRoleLogic) tapeReader;
        trGui = this;
    }
    
    public static TapeReaderGuiMain getTrGui() {
        return trGui;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public TrRoleLogic getTrRoleLogic() {
        return trRoleLogic;
    }
    
    public void init() {
        trRoleLogic.init();
        initTimer();
        createAndShowGUI();
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    void createAndShowGUI() {
        Security security = trRoleLogic.getSecurity("BTC/USDT", TickerType.BINANCE);
        buildChartPanel(security);
        buildLeftPanel();
        buildRightPanel();
        
        //Create and set up the window.
        frame = new JFrame("Tape Reader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        container = frame.getContentPane();
 
        //Set up the content pane.
        addComponentsToPane();
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        UIUtils.centerFrameOnScreen(frame);
    }
    
    public void initTimer() {
        timer = new Timer(Integer.parseInt(INTERVAL) * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marketDataPanel.updateTable(trRoleLogic.getCurrentTicks());
                marketDataPanel.filterSecurities();
            }
        });
        timer.start();
    }
    
    public void buildRightPanel() {
        // Right Panel
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setPreferredSize(new Dimension(450, 700));
        
        rightPanel.add(buildStrategyPanel());
        marketDataPanel = getMarketDataPanel();
        rightPanel.add(marketDataPanel);
        rightPanel.add(new PositionPanel(trRoleLogic.getAllLines()));
        rightPanel.add(new TransactionPanel());
    }
    
    public JPanel buildStrategyPanel() {
        JPanel strategyPanel = new JPanel();
        strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.LINE_AXIS));
        
        JLabel strategyLbl = new JLabel("Tip: ");
        strategyPanel.add(strategyLbl);
        strategyPanel.add(Box.createRigidArea(new Dimension(20,0)));
        List<Tip> tips = trRoleLogic.getAllTips();
        tipCombo = new JComboBox<>(tips.toArray(new Tip[tips.size()]));
        tipCombo.setSelectedItem(trRoleLogic.getTip());
        tipCombo.addActionListener(new StrategyComboListener());
        strategyPanel.add(tipCombo);
        
        strategyPanel.add(Box.createRigidArea(new Dimension(20,0)));
        JLabel shopLbl = new JLabel("Shop: ");
        strategyPanel.add(shopLbl);
        strategyPanel.add(Box.createRigidArea(new Dimension(20,0)));
        List<BucketShop> shops = trRoleLogic.getAllBucketShops();
        shopCombo = new JComboBox<>(shops.toArray(new BucketShop[shops.size()]));
        for (BucketShop shop : shops) {
            if (shop.getName().equals(TickerType.BINANCE.toString())){
                shopCombo.setSelectedItem(shop);
            }
        }
        shopCombo.addActionListener(new ShopComboListener());
        strategyPanel.add(shopCombo);
        return strategyPanel;
    }
    
    public MarketDataPanel getMarketDataPanel() {
        List<Tick> ticks = trRoleLogic.getCurrentTicks();
        List<Security> securities = trRoleLogic.getAllSecurities();
        List<MarketType> markets = trRoleLogic.getAllMarkets();
        return new MarketDataPanel(markets, securities, ticks);
    }
    
    public void buildLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setPreferredSize(new Dimension(900, 700));
        leftPanel.add(chartPanel);
        leftPanel.add(new OrderPanel());
    }
    
    public void buildChartPanel(Security security) {
        int lookback = trRoleLogic.getConfiguration().getLookback();
        LocalDateTime start = LocalDateTime.now().minusDays(lookback);
        trRoleLogic.storeHistoricalBars(security, start, LocalDateTime.now(), trRoleLogic.getConfiguration().getBarSize());
        
        List<Bar> bars = trRoleLogic.getBars(security);
        int ignoreDays = trRoleLogic.getConfiguration().getIgnoreBarDays();
        if (ignoreDays > 0 && bars.size() < lookback) {
            bars = bars.subList(ignoreDays, bars.size());
        }
        List<Bar> minMaxBars = bars.subList(bars.size() - 25, bars.size() - 1);
        Bar min = minMaxBars.stream().min(Comparator.comparing(Bar::getClose)).get();
        Bar max = minMaxBars.stream().max(Comparator.comparing(Bar::getClose)).get();
        
        TimeSeries series = trRoleLogic.buildTimeSeries(bars);
        
        // Building the trading strategy
        Strategy strategy = trRoleLogic.buildStrategy(series);
        
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(security.getSymbol(), 
                ChartUtils.createOHLCDataset(security.getSymbol(), series));
        
        ChartUtils.addMinMax(chart, min, max);
        
        //Running the strategy and adding the buy and sell signals to plot
        trRoleLogic.addBuySellSignals(series, strategy, chart);
        
        // Set the chart
        chartPanel = ChartUtils.newJFreeAppFrame(chart);
        
    }
    
    public void rebuildChart(String symbol) {
        SwingUtilities.invokeLater(() -> {
            String[] split = symbol.split(":");
            container.remove(leftPanel);
            buildChartPanel(trRoleLogic.getSecurity(split[0], TickerType.valueOf(split[1])));
            buildLeftPanel();
            addComponentsToPane();
            container.revalidate();
        });
    }
    
    public void addComponentsToPane() {
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        // Left Panel
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.6;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        container.add(leftPanel, c);
        
        // Right Panel
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.4;
        c.weighty = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        container.add(rightPanel, c);
    }
    
    private class StrategyComboListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            Tip tip = (Tip)cb.getSelectedItem();
            trRoleLogic.setTip(tip);
        }
        
    }
    
    private class ShopComboListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            marketDataPanel.filterSecurities();
        }
        
    }

    public String getShopName() {
        return ((BucketShop)shopCombo.getSelectedItem()).toString();
    }

    @Override
    public void terminate() {
        // TODO Auto-generated method stub
        
    }

}
