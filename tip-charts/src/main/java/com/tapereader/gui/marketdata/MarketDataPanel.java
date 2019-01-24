package com.tapereader.gui.marketdata;

import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.tapereader.marketdata.Tick;

public class MarketDataPanel extends JPanel {

    private static final long serialVersionUID = -179869562891160216L;
    
    private MarketDataTable marketTable;
    
    public MarketDataPanel(List<Tick> ticks) {
        this.marketTable = new MarketDataTable(new MarketDataTableModel(ticks));
        buildGui();
    }
    
    private void buildGui() {
        JScrollPane scrollPane = new JScrollPane(marketTable);
        add(scrollPane);
    }
    
    public void filterSecurities(String marketName, String shopName) {
        SwingUtilities.invokeLater(() -> {
            marketTable.setFilter(marketName, shopName);
        });
    }

    public void updateTable(List<Tick> ticks) {
        marketTable.updateTicks(ticks);
    }

}
