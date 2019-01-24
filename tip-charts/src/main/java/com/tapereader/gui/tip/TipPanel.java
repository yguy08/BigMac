package com.tapereader.gui.tip;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tapereader.enumeration.MarketType;
import com.tapereader.enumeration.TickerType;
import com.tapereader.gui.TapeReaderGuiMain;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Tip;

public class TipPanel extends JPanel implements ActionListener {
    
    private JLabel tipLbl;
    
    private JLabel shopLbl;
    
    private JLabel marketLbl;
    
    private JComboBox<BucketShop> shopCombo;
    
    private JComboBox<Tip> tipCombo;
    
    private JComboBox<MarketType> marketCombo;
    
    public TipPanel(List<Tip> tips, List<BucketShop> shops, List<MarketType> markets) {
        setLayout(new FlowLayout());
        
        tipLbl = new JLabel("Tip: ");
        add(tipLbl);
        
        tipCombo = new JComboBox<>(tips.toArray(new Tip[tips.size()]));
        tipCombo.setSelectedItem(tips.get(0));
        add(tipCombo);
        tipCombo.addActionListener(this);
        
        shopLbl = new JLabel("Shop: ");
        add(shopLbl);
        shopCombo = new JComboBox<>(shops.toArray(new BucketShop[shops.size()]));
        for (BucketShop shop : shops) {
            if (shop.getName().equals(TickerType.BINANCE.toString())){
                shopCombo.setSelectedItem(shop);
            }
        }
        shopCombo.addActionListener(this);
        add(shopCombo);
        
        marketLbl = new JLabel("Market: ");
        add(marketLbl);
        marketCombo = new JComboBox<>(markets.toArray(new MarketType[markets.size()]));
        marketCombo.setSelectedItem(MarketType.BTC);
        marketCombo.addActionListener(this);
        add(marketCombo);
    }
    
    public String getMarket() {
        return marketCombo.getSelectedItem().toString();
    }
    
    public String getTip() {
        return tipCombo.getSelectedItem().toString();
    }
    
    public String getShop() {
        return shopCombo.getSelectedItem().toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == tipCombo) {
            TapeReaderGuiMain.getTrGui().setTip(getTip());
        } else {
            TapeReaderGuiMain.getTrGui().resetFilter(getMarket(), getShop());
        }
    }
}
