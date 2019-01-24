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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.tapereader.clerk.Clerk;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.gui.chart.ChartUtils;
import com.tapereader.gui.chart.TRChartPanel;
import com.tapereader.gui.marketdata.MarketDataPanel;
import com.tapereader.gui.tip.TipPanel;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Security;
import com.tapereader.model.Tip;
import com.tapereader.tip.TapeReader;
import com.tapereader.tip.SwingTip;

public class TapeReaderGuiMain implements Clerk {
    
    private JFrame frame;
    
    private static TapeReaderGuiMain trGui;
    
    private ChartPanel chartPanel;
    
    private Container container;
    
    private JPanel rightPanel;
    
    private TRChartPanel trChartPanel;
    
    private MarketDataPanel marketDataPanel;
    
    private SwingTip swingTip;
    
    private TipPanel tipPanel;
    
    @Inject
    @Named("apprefresh")
    private String INTERVAL;
    
    private Timer timer;
    
    @Inject
    private TapeReaderGuiMain(TapeReader tapeReader) {
        this.swingTip = (SwingTip) tapeReader;
        trGui = this;
    }
    
    public static TapeReaderGuiMain getTrGui() {
        return trGui;
    }
    
    public void init() {
        swingTip.init();
        initTimer();
        createAndShowGUI();
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    void createAndShowGUI() {
        Security security = swingTip.getSecurity("BTC/USDT", TickerType.BINANCE);
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
                marketDataPanel.updateTable(swingTip.getCurrentTicks());
            }
        });
        timer.start();
    }
    
    public void buildRightPanel() {
        // Right Panel
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(5, 5));
        
        // Strategy Panel
        tipPanel = buildTipPanel();
        rightPanel.add(tipPanel, BorderLayout.PAGE_START);
        
        //market data panel
        List<Tick> ticks = swingTip.getCurrentTicks();
        marketDataPanel = new MarketDataPanel(ticks);
        marketDataPanel.filterSecurities(tipPanel.getMarket(), tipPanel.getShop());
        rightPanel.add(marketDataPanel, BorderLayout.CENTER);
    }

    public TipPanel buildTipPanel() {
        List<Tip> tips = swingTip.getAllTips();
        List<BucketShop> shops = swingTip.getAllBucketShops();
        List<MarketType> markets = swingTip.getAllMarkets();
        return new TipPanel(tips, shops, markets);
    }
    
    public void buildChartPanel(Security security) {
        trChartPanel = new TRChartPanel();
        
        int lookback = swingTip.getConfiguration().getLookback();
        Instant start = Instant.now().minus(lookback, ChronoUnit.DAYS);
        swingTip.storeHistoricalBars(security, start, Instant.now(), swingTip.getConfiguration().getBarSize());
        
        List<Bar> bars = swingTip.getBars(security);
        int ignoreDays = swingTip.getConfiguration().getIgnoreBarDays();
        int barSize = bars.size();
        if (ignoreDays > 0 && barSize < lookback) {
            bars = bars.subList(ignoreDays, barSize);
            barSize = bars.size();
        }
        int minMax = barSize - 25 > 0 ? barSize - 25 : 0;
        List<Bar> minMaxBars = bars.subList(minMax, barSize);
        Bar min = minMaxBars.stream().min(Comparator.comparing(Bar::getClose)).get();
        Bar max = minMaxBars.stream().max(Comparator.comparing(Bar::getClose)).get();
        
        TimeSeries series = swingTip.buildTimeSeries(bars);
        
        // Building the trading strategy
        Strategy strategy = swingTip.buildStrategy(series);
        
        //Building chart datasets
        JFreeChart chart = ChartUtils.newCandleStickChart(security.getSymbol(), 
                ChartUtils.createOHLCDataset(security.getSymbol(), series));
        
        ChartUtils.addMinMax(chart, min, max);
        
        //Running the strategy and adding the buy and sell signals to plot
        swingTip.addBuySellSignals(series, strategy, chart);
        
        // Set the chart
        chartPanel = ChartUtils.newJFreeAppFrame(chart);
        
        writeChartImage(chart);
        
        trChartPanel.add(chartPanel);
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
            e.printStackTrace();
        }
    }
    
    public void rebuildChart(String symbol) {
        SwingUtilities.invokeLater(() -> {
            String[] split = symbol.split(":");
            container.remove(trChartPanel);
            buildChartPanel(swingTip.getSecurity(split[0], TickerType.valueOf(split[1])));
            addComponentsToPane();
            container.revalidate();
        });
    }
    
    public void addComponentsToPane() {
        container.setLayout(new BorderLayout());
        container.add(trChartPanel, BorderLayout.CENTER);
        container.add(rightPanel, BorderLayout.LINE_END);
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

}
