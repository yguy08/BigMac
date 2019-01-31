package com.tapereader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.tapereader.config.Config;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.gui.utils.ListTableModel;
import com.tapereader.gui.utils.MarketDataTableMapper;
import com.tapereader.gui.utils.TRTable;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.tip.Tip;
import com.tapereader.tip.TipClerk;

public class TRGuiMain {
    
    private final JFrame mainFrame;
    
    private JComboBox<TipType> tipCombo;
    
    private JComboBox<TickerType> tickerCombo;
    
    private JComboBox<MarketType> marketCombo;
    
    private TRTable trTable;
    
    private ChartPanel jfreeChartPanel;
    
    private TipClerk tipClerk;
    
    private JPanel toolBarPanel;
    
    public TRGuiMain(TipClerk tipClerk) {
        this.mainFrame = new JFrame("Bucketshop");
        this.tipClerk = tipClerk;
    }
    
    private void createBaseGui() {
        ActionListener comboListener = new TRComboBoxListener();
        List<TipType> tips = Arrays.asList(TipType.values()).stream().collect(Collectors.toList());
        tipCombo = new JComboBox<>(tips.toArray(new TipType[tips.size()]));
        tipCombo.addActionListener(comboListener);
        
        List<TickerType> tickers = Arrays.asList(TickerType.values()).stream().collect(Collectors.toList());
        tickerCombo = new JComboBox<TickerType>(tickers.toArray(new TickerType[tickers.size()]));
        tickerCombo.setSelectedItem(tipClerk.getConfig().getTickerType());
        tickerCombo.addActionListener(comboListener);
        
        List<MarketType> markets = Arrays.asList(MarketType.values()).stream().collect(Collectors.toList());
        marketCombo = new JComboBox<MarketType>(markets.toArray(new MarketType[markets.size()]));
        marketCombo.setSelectedItem(tipClerk.getConfig().getMarketType());
        marketCombo.addActionListener(comboListener);
        
        jfreeChartPanel = initChartPanel();
        
        List<Tick> ticks = tipClerk.getMarketDataClerk().getCurrentTicks();
        JPanel marketDataPanel = new JPanel(new BorderLayout(5, 5));
        ListTableModel model = new ListTableModel(new MarketDataTableMapper());
        model.setElements(ticks);
        trTable = new TRTable(model);
        trTable.addMouseListener(new MarketDataTableListener());
        JScrollPane scrollPane = new JScrollPane(trTable);
        marketDataPanel.add(scrollPane, BorderLayout.CENTER);
        
        toolBarPanel = new JPanel(new BorderLayout(5, 5));
        JToolBar toolBar = new JToolBar();
        toolBar.add(tipCombo);
        toolBar.add(tickerCombo);
        toolBar.add(marketCombo);
        toolBarPanel.add(toolBar);
        
        JPanel marketFeedPanel = new JPanel(new BorderLayout(5, 5));
        marketFeedPanel.add(marketDataPanel, BorderLayout.CENTER);
        
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
        contentPane.add(toolBarPanel, BorderLayout.PAGE_START);
        contentPane.add(jfreeChartPanel, BorderLayout.CENTER);
        contentPane.add(marketFeedPanel, BorderLayout.LINE_END);
        getMainJFrame().setContentPane(contentPane);
    }
    
    public void runGui() {
        createBaseGui();
        getMainJFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    public void init() {
        resetFilter();
    }
    
    private ChartPanel initChartPanel() {
        // tip clerk get config
        Config config = tipClerk.getConfig();
        Instant start = Instant.now().minusSeconds(config.getLookback() * config.getBarSize().getSeconds());
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().build();
        List<Bar> bars = tipClerk.getHistoricalDataClerk()
                .getHistoricalBars(config.getDefaultSymbol(), config.getTickerType(), start, Instant.now(), config.getBarSize());
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        //Building chart datasets
        JFreeChart chart = tipClerk.getTip().buildJFreeChart(config.getDefaultSymbol(), series);
        
        // Set the chart
        jfreeChartPanel = buildChartPanel(chart);
        return jfreeChartPanel;
    }
    
    private void rebuildChart() {
        // tip clerk get config
        Config config = tipClerk.getConfig();
        Instant start = Instant.now().minusSeconds(config.getLookback() * config.getBarSize().getSeconds());
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().build();
        List<Bar> bars = tipClerk.getHistoricalDataClerk()
                .getHistoricalBars(config.getDefaultSymbol(), config.getTickerType(), start, Instant.now(), config.getBarSize());
        for (Bar b : bars) {
            series.addBar(Instant.ofEpochMilli(b.getTimestamp()).atZone(ZoneOffset.UTC),
                    b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume());
        }
        //Building chart datasets
        JFreeChart chart = tipClerk.getTip().buildJFreeChart(config.getDefaultSymbol(), series);
        chart.addChangeListener(jfreeChartPanel);
        jfreeChartPanel.setChart(chart);
        chart.fireChartChanged();
    }
    
    /**
     * Displays a chart in a frame.
     * 
     * @param chart
     *            the chart to be displayed
     */
    private ChartPanel buildChartPanel(JFreeChart chart) {
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setOpaque(false);
        panel.setZoomOutlinePaint(Color.GREEN);
        panel.setDisplayToolTips(true);
        return panel;
    }
    
    public void resetFilter() {
        SwingUtilities.invokeLater(() -> {
            trTable.setFilter(tipClerk.getConfig().getMarketType().toString());
        });
    }
    
    private void updateTable(List<?> elements) {
        ((ListTableModel) trTable.getModel()).setElements(elements);
        ((ListTableModel) trTable.getModel()).fireTableDataChanged();
    }
    
    public void newTicker() {
        getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(() -> {
            updateTable(tipClerk.getMarketDataClerk().getCurrentTicks(tipClerk.getConfig().getTickerType()));
            getContainer().setCursor(null);
        });
    }
    
    private class TRComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            if (cb == tipCombo) {
                String tip = cb.getSelectedItem().toString();
                tipClerk.setTip(Tip.makeFactory(TipType.valueOf(tip)));
            } else if (cb == tickerCombo) {
                String ticker = cb.getSelectedItem().toString();
                tipClerk.getConfig().setTickerType(TickerType.valueOf(ticker));
                newTicker();
            } else if (cb == marketCombo){
                tipClerk.getConfig().setMarketType(MarketType.valueOf(marketCombo.getSelectedItem().toString()));
                resetFilter();
            } else {
                
            }
        }
    }
    
    private class MarketDataTableListener extends MouseAdapter {
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() > 1) {
                List<Tick> ticks = (List<Tick>) ((ListTableModel) trTable.getModel()).getElements();
                int i = trTable.convertRowIndexToModel(trTable.getSelectedRow());
                Tick tick = ticks.get(i);
                tipClerk.getConfig().setDefaultSymbol(tick.getSymbol());
                rebuildChart();
            }
        }
    }
}
