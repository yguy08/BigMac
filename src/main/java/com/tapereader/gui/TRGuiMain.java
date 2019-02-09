package com.tapereader.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.ta4j.core.TimeSeries;

import com.tapereader.chart.ChartManager;
import com.tapereader.chart.TipClerk;
import com.tapereader.chart.strategy.buyhigh.BuyHigh;
import com.tapereader.enumeration.BarSize;
import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.enumeration.TipType;
import com.tapereader.gui.utils.ListTableModel;
import com.tapereader.gui.utils.MarketDataTableMapper;
import com.tapereader.gui.utils.TRChartPanel;
import com.tapereader.gui.utils.TRTable;
import com.tapereader.marketdata.Tick;

public class TRGuiMain implements ChangeListener {
    
    private final JFrame mainFrame;
    
    private JComboBox<TipType> tipCombo;
    
    private JComboBox<TickerType> tickerCombo;
    
    private JComboBox<MarketType> marketCombo;
    
    private JComboBox<BarSize> barSize;
    
    private JSlider lookBackSlider;
    
    private JTextArea textArea;
    
    private TRTable trTable;
    
    private TRChartPanel chartPanel;
    
    private TipClerk tipClerk;
    
    private JPanel toolBarPanel;
    
    public TRGuiMain(TipClerk tipClerk) {
        this.mainFrame = new JFrame("Bucketshop");
        this.tipClerk = tipClerk;
    }
    
    private void createBaseGui() {
        ActionListener comboListener = new TRComboBoxListener();
        tipCombo = new JComboBox<>(TipType.values());
        tipCombo.addActionListener(comboListener);
        
        tickerCombo = new JComboBox<TickerType>(TickerType.values());
        tickerCombo.setSelectedItem(tipClerk.getConfig().getTickerType());
        tickerCombo.addActionListener(comboListener);
        
        marketCombo = new JComboBox<MarketType>(MarketType.values());
        marketCombo.setSelectedItem(tipClerk.getConfig().getMarketType());
        marketCombo.addActionListener(comboListener);
        
        barSize = new JComboBox<BarSize>(BarSize.values());
        barSize.setSelectedItem(BarSize.d1);
        barSize.addActionListener(comboListener);
        
        lookBackSlider = new JSlider(JSlider.HORIZONTAL,
                1, 1000, 150);
        //Turn on labels at major tick marks.
        lookBackSlider.setMajorTickSpacing(100);
        lookBackSlider.setMinorTickSpacing(50);
        lookBackSlider.setPaintTicks(true);
        lookBackSlider.setPaintLabels(true);
        lookBackSlider.addChangeListener(this);
        
        ChartManager chartManager = tipClerk.getChartManager();
        TimeSeries series = tipClerk.buildTimeSeries();
        chartManager.setTimeSeries(series);
        JFreeChart chart = chartManager.buildChart(tipClerk.getChartStrategy());
        // Set the chart
        chartPanel = new TRChartPanel(chart);
        
        List<Tick> ticks = tipClerk.getMarketDataClerk().getCurrentTicks();
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
        
        toolBarPanel = new JPanel(new BorderLayout(5, 5));
        JToolBar toolBar = new JToolBar();
        toolBar.add(tipCombo);
        toolBar.add(tickerCombo);
        toolBar.add(marketCombo);
        toolBar.add(barSize);
        toolBar.add(lookBackSlider);
        toolBarPanel.add(toolBar);
        
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
        setStrategyAnalysis();
    }
    
    private JFrame getMainJFrame() {
        return mainFrame;
    }
    
    private Container getContainer() {
        return mainFrame.getContentPane();
    }
    
    private void buildChart() {
        ChartManager chartManager = tipClerk.getChartManager();
        TimeSeries series = tipClerk.buildTimeSeries();
        chartManager.setTimeSeries(series);
        JFreeChart chart = chartManager.buildChart(tipClerk.getChartStrategy());
        chartPanel.rebuildChart(chart);
    }
    
    private void setStrategyAnalysis() {
        textArea.setText(tipClerk.getChartManager().getStrategyAnalysis(tipClerk.getChartStrategy()));
        textArea.setCaretPosition(0);
    }
    
    private void resetFilter() {
        SwingUtilities.invokeLater(() -> {
            trTable.setFilter(tipClerk.getConfig().getMarketType().toString());
        });
    }
    
    private void updateTable(List<?> elements) {
        ((ListTableModel) trTable.getModel()).setElements(elements);
        ((ListTableModel) trTable.getModel()).fireTableDataChanged();
    }
    
    private void newTicker() {
        getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(() -> {
            updateTable(tipClerk.getMarketDataClerk().getCurrentTicks(tipClerk.getConfig().getTickerType()));
            getContainer().setCursor(null);
        });
    }
    
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            int fps = (int)source.getValue();
            tipClerk.getConfig().setLookback(fps);
            buildChart();
            setStrategyAnalysis();
        }
    }
    
    private class TRComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            if (cb == tipCombo) {
                String tip = cb.getSelectedItem().toString();
                tipClerk.setChartStrategy(new BuyHigh());
            } else if (cb == tickerCombo) {
                String ticker = cb.getSelectedItem().toString();
                tipClerk.getConfig().setTickerType(TickerType.valueOf(ticker));
                newTicker();
            } else if (cb == marketCombo){
                tipClerk.getConfig().setMarketType(MarketType.valueOf(marketCombo.getSelectedItem().toString()));
                resetFilter();
            } else if (cb == barSize){
                BarSize barSize = ((BarSize) cb.getSelectedItem());
                tipClerk.getConfig().setBarSize(Duration.ofMillis(barSize.getMillis()));
                buildChart();
                setStrategyAnalysis();
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
                buildChart();
                setStrategyAnalysis();
            }
        }
    }
}
