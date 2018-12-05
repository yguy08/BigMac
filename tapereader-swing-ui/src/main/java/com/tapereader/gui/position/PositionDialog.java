package com.tapereader.gui.position;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.tapereader.enumeration.TickerType;
import com.tapereader.model.Line;

public class PositionDialog extends JDialog implements ActionListener, PropertyChangeListener {
    
    private static final long serialVersionUID = -1689728153145438377L;

    private JOptionPane optionPane;
    
    private JTextField symbol;
    
    private JTextField exchange;
    
    private JTextField quantity;
    
    private JTextField price;
    
    public PositionDialog(Frame aFrame) {
        super(aFrame, "Enter Position", true);
        
        symbol = new JTextField("Symbol", 10);
        exchange = new JTextField("Exchange", 10);
        quantity = new JTextField("Quantity", 10);
        price = new JTextField("Price", 10);
        
        Object[] array = {symbol, exchange, quantity, price};
        Object[] options = {"OK", "Cancel"};
        optionPane = new JOptionPane(array, 
                                            JOptionPane.PLAIN_MESSAGE,
                                            JOptionPane.YES_NO_OPTION,
                                            null,
                                            options,
                                            options[0]);
        
        setContentPane(optionPane);
        //Handle window closing correctly.
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });
        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                symbol.requestFocusInWindow();
            }
        });
        
        //Register an event handler that puts the text into the option pane.
        symbol.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }
    
    public Line getPosition() {
        try {
            String s = symbol.getText().toUpperCase();
            TickerType ex = TickerType.valueOf(exchange.getText().toUpperCase());
            double q = Double.parseDouble(quantity.getText());
            double p = Double.parseDouble(price.getText());
            return new Line();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        clearAndHide();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        setVisible(false);
    }

}
