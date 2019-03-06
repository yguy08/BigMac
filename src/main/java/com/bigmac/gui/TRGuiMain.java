package com.bigmac.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import com.bigmac.chart.ChartUtils;
import com.bigmac.chart.strategy.ChartStrategy;
import com.bigmac.chart.strategy.ChartStrategyFactory;
import com.bigmac.enumeration.BarSize;
import com.bigmac.enumeration.LookbackPeriod;
import com.bigmac.enumeration.MarketType;
import com.bigmac.enumeration.TickerType;
import com.bigmac.enumeration.TipType;
import com.bigmac.gui.chart.TRChartPanel;
import com.bigmac.gui.controller.TipClerk;
import com.bigmac.marketdata.Tick;

public class TRGuiMain implements ActionListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TRGuiMain.class);
    
    private final JFrame mainFrame;
    
    private JComboBox<TipType> tipCombo;
    
    private JComboBox<TickerType> tickerCombo;
    
    private JComboBox<MarketType> marketCombo;
    
    private JComboBox<BarSize> barSizeCombo;
    
    private JTextArea textArea;
    
    private TRTable trTable;
    
    private TRChartPanel chartPanel;
    
    private TipClerk tipClerk;
    
    private JPanel toolBarPanel;
    
    private static final String APP_TITLE = "Big Mac";
    
    public TRGuiMain(TipClerk tipClerk) {
        this.mainFrame = new JFrame(APP_TITLE);
        this.tipClerk = tipClerk;
    }
    
    private void createBaseGui() {
        tipCombo = new JComboBox<>(TipType.values());
        tipCombo.addActionListener(this);
        
        tickerCombo = new JComboBox<TickerType>(TickerType.values());
        tickerCombo.setSelectedItem(tipClerk.getConfig().getTickerType());
        tickerCombo.addActionListener(this);
        
        marketCombo = new JComboBox<MarketType>(MarketType.values());
        marketCombo.setSelectedItem(tipClerk.getConfig().getMarketType());
        marketCombo.addActionListener(this);
        
        barSizeCombo = new JComboBox<BarSize>(BarSize.values());
        barSizeCombo.setSelectedItem(BarSize.d1);
        barSizeCombo.addActionListener(this);
        
        // clear tick cache
        tipClerk.getCacheClerk().clearTickCache();
        List<Tick> ticks = tipClerk.getMarketDataClerk().getCurrentTicks(tipClerk.getConfig().getTickerType());
        
        JPanel marketDataPanel = new JPanel(new BorderLayout(5, 5));
        
        ListTableModel model = new ListTableModel(new MarketDataTableMapper());
        model.setElements(ticks);
        trTable = new TRTable(model);
        trTable.addMouseListener(new MarketDataTableListener());
        JScrollPane tblScrollPane = new JScrollPane(trTable);
        
        textArea = new JTextArea(10, 20);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        JScrollPane txtScrollPane = new JScrollPane(textArea);
        
        marketDataPanel.add(tblScrollPane, BorderLayout.CENTER);
        marketDataPanel.add(txtScrollPane, BorderLayout.PAGE_END);
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setBusy(true);
                // clear tick cache
                tipClerk.getCacheClerk().clearTickCache();
                List<Tick> ticks = tipClerk.getMarketDataClerk().getCurrentTicks(tipClerk.getConfig().getTickerType());
                updateTable(ticks);
                setBusy(false);
            }
        });
        
        JButton chartBtn = new JButton("Chart");
        chartBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChartSettingDialog dialog = new ChartSettingDialog(getMainJFrame());
                dialog.pack();
                dialog.setVisible(true);
                buildChart();
            }
        });
        
        toolBarPanel = new JPanel(new BorderLayout(5, 5));
        JToolBar toolBar = new JToolBar();
        JPanel comboPanel = new JPanel();
        comboPanel.add(tipCombo);
        comboPanel.add(tickerCombo);
        comboPanel.add(marketCombo);
        comboPanel.add(barSizeCombo);
        toolBar.add(comboPanel);
        
        JPanel lookbackPanel = new JPanel();
        for (LookbackPeriod period : LookbackPeriod.values()) {
            JButton jbutton = createJButton(period.toString());
            jbutton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String periodStr = ((JButton) e.getSource()).getText();
                    LookbackPeriod period = LookbackPeriod.valueOf(periodStr);
                    tipClerk.getConfig().setLookback(period.getPeriod());
                    buildChart();
                    setStrategyAnalysis();
                }
            });
            lookbackPanel.add(jbutton);
        }
        toolBar.add(lookbackPanel);
        
        JPanel refreshPanel = new JPanel();
        refreshPanel.add(chartBtn);
        refreshPanel.add(refresh);
        toolBar.add(refreshPanel);
        toolBarPanel.add(toolBar);
        
        buildChart();
        
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
        contentPane.add(toolBarPanel, BorderLayout.PAGE_START);
        contentPane.add(chartPanel, BorderLayout.CENTER);
        contentPane.add(marketDataPanel, BorderLayout.LINE_END);
        
        JFrame frame = getMainJFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.setContentPane(contentPane);
    }
    
    public void createAndShowGui() {
        // Create Base GUI
        createBaseGui();
        
        JFrame frame = getMainJFrame();
        frame.pack();
        frame.setVisible(true);
        UIUtils.centerFrameOnScreen(frame);
        
        resetFilter();
    }
    
    private JFrame getMainJFrame() {
        return mainFrame;
    }
    
    private Container getContainer() {
        return mainFrame.getContentPane();
    }
    
    private void buildChart() {
        ChartStrategy strategy = tipClerk.getChartStrategy();
        TimeSeries series = tipClerk.buildTimeSeries();
        LOGGER.debug("Building Chart with TimeSeries count of {}", series.getBarCount());
        strategy.setSeries(series);
        JFreeChart chart = ChartUtils.buildChart(strategy);
        if (chartPanel != null) {
            chartPanel.rebuildChart(chart);
        } else {
            chartPanel = new TRChartPanel(chart);
        }
        setStrategyAnalysis();
    }
    
    private void setStrategyAnalysis() {
        textArea.setText(tipClerk.getChartStrategy().getStrategyAnalysis());
        textArea.setCaretPosition(0);
    }
    
    private void resetFilter() {
        SwingUtilities.invokeLater(() -> {
            trTable.setFilter(tipClerk.getConfig().getMarketType().toString());
            ((ListTableModel) trTable.getModel()).fireTableDataChanged();
        });
    }
    
    private void updateTable(List<?> elements) {
        SwingUtilities.invokeLater(() -> {
            ListTableModel model = (ListTableModel) trTable.getModel();
            model.setElements(elements);
            trTable.setModel(model);
            ((ListTableModel) trTable.getModel()).fireTableDataChanged();
            // comment out if breaks
            resetFilter();
        });
    }
    
    private void newTicker() {
        getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(() -> {
            List<Tick> ticks = tipClerk.getCacheClerk().getCurrentTicks(tipClerk.getConfig().getTickerType());
            if (ticks == null || ticks.isEmpty()) {
                ticks = tipClerk.getMarketDataClerk().getCurrentTicks(tipClerk.getConfig().getTickerType());
            }
            updateTable(ticks);
            getContainer().setCursor(null);
        });
    }
    
    private void setBusy(boolean busy) {
        if (busy) {
            getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            getContainer().setCursor(null);
        }
    }
    
    private JButton createJButton(String period) {
        JButton lBtn = new JButton(period.toString());
        return lBtn;
    }
    
    private class MarketDataTableListener extends MouseAdapter {
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() > 1) {
                List<Tick> ticks = (List<Tick>) ((ListTableModel) trTable.getModel()).getElements();
                int i = trTable.convertRowIndexToModel(trTable.getSelectedRow());
                Tick tick = ticks.get(i);
                tipClerk.getConfig().setDefaultSymbol(tick.getSymbol().toCurrencyPairString());
                buildChart();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>)e.getSource();
            if (cb == tipCombo) {
                TipType tip = (TipType) cb.getSelectedItem();
                ChartStrategy strategy = ChartStrategyFactory.buildChartStrategy(tip, 
                        new BaseTimeSeries.SeriesBuilder().withName(tipClerk.getConfig().getDefaultSymbol()).build());
                tipClerk.setChartStrategy(strategy);
                buildChart();
            } else if (cb == tickerCombo) {
                TickerType ticker = (TickerType) cb.getSelectedItem();
                if (TickerType.CPRO.equals(ticker)) {
                    tipClerk.getConfig().setMarketType(MarketType.USD);
                } else if (MarketType.USD.equals(marketCombo.getSelectedItem())) {
                    tipClerk.getConfig().setMarketType(MarketType.BTC);
                }
                tipClerk.getConfig().setTickerType(ticker);
                newTicker();
                resetFilter();
            } else if (cb == marketCombo){
                tipClerk.getConfig().setMarketType((MarketType) marketCombo.getSelectedItem());
                resetFilter();
            } else if (cb == barSizeCombo){
                BarSize barSize = ((BarSize) cb.getSelectedItem());
                tipClerk.getConfig().setBarSize(Duration.ofMillis(barSize.getMillis()));
                buildChart();
            }
        }
    }
}
