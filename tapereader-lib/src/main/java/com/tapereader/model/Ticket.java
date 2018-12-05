package com.tapereader.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.tapereader.enumeration.TransactionType;

@Entity
public class Ticket {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="LINE_ID")
    private Line line;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SECURITY_ID")
    private Security security;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
    
    private long timestamp;
    
    private TransactionType transactionType;
    
    private double quantity;
    
    private double price;
    
    public Ticket() {
        
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
