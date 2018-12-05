package com.tapereader.gui.marketdata;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.tapereader.enumeration.MarketType;
import com.tapereader.gui.TapeReaderGuiMain;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Security;

public class MarketDataPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -179869562891160216L;
    
    private MarketDataTable marketTable;
    
    private JComboBox<MarketType> marketCombo;
    
    private JComboBox<Security> securityCombo;
    
    private List<Security> securities;
    
    private List<MarketType> markets;
    
    public MarketDataPanel(List<MarketType> markets, List<Security> securities, List<Tick> ticks) {
        this.markets = markets;
        this.securities = securities;
        this.marketTable = new MarketDataTable(new MarketDataTableModel(ticks));
        buildGui();
    }
    
    public void buildGui() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(450, 250));
        
        // Label and subscribe combo
        JPanel subscribePanel = buildSubscribePanel();
        //build combos
        marketCombo = buildMarketCombo();
        marketCombo.addActionListener(this);
        securityCombo = buildSecuritiesCombo();
        securityCombo.addActionListener(this);
        subscribePanel.add(marketCombo);
        subscribePanel.add(securityCombo);
        add(subscribePanel);
        
        JScrollPane scrollPane = new JScrollPane(marketTable);
        add(scrollPane);
        
        filterSecurities();
    }
    
    public JPanel buildSubscribePanel() {
        JPanel subscribePanel = new JPanel();
        subscribePanel.setLayout(new BoxLayout(subscribePanel, BoxLayout.LINE_AXIS));
        JLabel marketDatalbl = new JLabel("Market: ");
        subscribePanel.add(marketDatalbl);
        return subscribePanel;
    }
    
    private JComboBox<MarketType> buildMarketCombo() {
        return new JComboBox<>(markets.toArray(new MarketType[markets.size()]));
    }
    
    private JComboBox<Security> buildSecuritiesCombo() {
        return new JComboBox<>(securities.toArray(new Security[securities.size()]));
    }
    
    public void filterSecurities() {
        String marketName = marketCombo.getSelectedItem().toString();
        String shopName = TapeReaderGuiMain.getTrGui().getShopName();
        SwingUtilities.invokeLater(() -> {
            marketTable.setFilter(marketName, shopName);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        filterSecurities();
    }

    public void updateTable(List<Tick> ticks) {
        marketTable.updateTicks(ticks);
    }

}
