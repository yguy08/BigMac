package com.tapereader.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
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
    
    private JComboBox<MarketType> marketCombo;
    
    @Inject
    private TapeReaderGuiMain(TapeReader tapeReader) {
        this.trRoleLogic = (TrRoleLogic) tapeReader;
        trGui = this;
    }
    
    public static TapeReaderGuiMain getTrGui() {
        return trGui;
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
            }
        });
        timer.start();
    }
    
    public void buildRightPanel() {
        // Right Panel
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(5, 5));
        
        // Strategy Panel
        rightPanel.add(buildStrategyPanel(), BorderLayout.PAGE_START);
        
        //market data panel
        List<Tick> ticks = trRoleLogic.getCurrentTicks();
        marketDataPanel = new MarketDataPanel(ticks);
        marketDataPanel.filterSecurities(marketCombo.getSelectedItem().toString());
        rightPanel.add(marketDataPanel, BorderLayout.CENTER);
    }

    public JPanel buildStrategyPanel() {
        JPanel strategyPanel = new JPanel();
        strategyPanel.setLayout(new FlowLayout());
        ComboListener comboListener = new ComboListener();
        JLabel strategyLbl = new JLabel("Tip: ");
        strategyPanel.add(strategyLbl);
        List<Tip> tips = trRoleLogic.getAllTips();
        tipCombo = new JComboBox<>(tips.toArray(new Tip[tips.size()]));
        tipCombo.setSelectedItem(trRoleLogic.getTip());
        tipCombo.addActionListener(comboListener);
        strategyPanel.add(tipCombo);
        
        JLabel shopLbl = new JLabel("Shop: ");
        strategyPanel.add(shopLbl);
        List<BucketShop> shops = trRoleLogic.getAllBucketShops();
        shopCombo = new JComboBox<>(shops.toArray(new BucketShop[shops.size()]));
        for (BucketShop shop : shops) {
            if (shop.getName().equals(TickerType.BINANCE.toString())){
                shopCombo.setSelectedItem(shop);
            }
        }
        shopCombo.addActionListener(comboListener);
        strategyPanel.add(shopCombo);
        
        JLabel marketLbl = new JLabel("Market: ");
        strategyPanel.add(marketLbl);
        List<MarketType> markets = trRoleLogic.getAllMarkets();
        marketCombo = new JComboBox<>(markets.toArray(new MarketType[markets.size()]));
        marketCombo.setSelectedItem(MarketType.BTC);
        marketCombo.addActionListener(comboListener);
        strategyPanel.add(marketCombo);
        return strategyPanel;
    }
    
    public void buildChartPanel(Security security) {
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(1000, 600));
        
        int lookback = trRoleLogic.getConfiguration().getLookback();
        LocalDateTime start = LocalDateTime.now().minusDays(lookback);
        trRoleLogic.storeHistoricalBars(security, start, LocalDateTime.now(), trRoleLogic.getConfiguration().getBarSize());
        
        List<Bar> bars = trRoleLogic.getBars(security);
        int ignoreDays = trRoleLogic.getConfiguration().getIgnoreBarDays();
        int barSize = bars.size();
        if (ignoreDays > 0 && barSize < lookback) {
            bars = bars.subList(ignoreDays, barSize);
            barSize = bars.size();
        }
        int minMax = barSize - 25 > 0 ? barSize - 25 : 0;
        List<Bar> minMaxBars = bars.subList(minMax, barSize);
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
        
        writeChartImage(chart);
        
        leftPanel.add(chartPanel);
    }
    
    public void writeChartImage(JFreeChart chart) {
        BufferedImage objBufferedImage=chart.createBufferedImage(900, 700);
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
                try {
                    ImageIO.write(objBufferedImage, "png", bas);
                } catch (IOException e) {
                    e.printStackTrace();
                }

        byte[] byteArray = bas.toByteArray();
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage image = ImageIO.read(in);
            File outputfile = new File("c:\\tmp\\chart.png");
            ImageIO.write(image, "png", outputfile);
            System.out.println("Wrote Chart!");
        } catch (Exception e) {
            
        }
    }
    
    public void rebuildChart(String symbol) {
        SwingUtilities.invokeLater(() -> {
            String[] split = symbol.split(":");
            container.remove(leftPanel);
            buildChartPanel(trRoleLogic.getSecurity(split[0], TickerType.valueOf(split[1])));
            addComponentsToPane();
            container.revalidate();
        });
    }
    
    public void addComponentsToPane() {
        container.setLayout(new BorderLayout());
        container.add(leftPanel, BorderLayout.CENTER);
        container.add(rightPanel, BorderLayout.LINE_END);
    }
    
    private class ComboListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if (obj == tipCombo) {
                Tip tip = (Tip) tipCombo.getSelectedItem();
                trRoleLogic.setTip(tip);
            } else if (obj == shopCombo) {
                marketDataPanel.filterSecurities(marketCombo.getSelectedItem().toString());
            } else if (obj == marketCombo) {
                MarketType marketType = (MarketType) marketCombo.getSelectedItem();
                String marketName = marketType.toString();
                marketDataPanel.filterSecurities(marketName);
            }
        }
        
    }

    public String getShopName() {
        return ((BucketShop)shopCombo.getSelectedItem()).toString();
    }

    @Override
    public void terminate() {
        
    }

}
