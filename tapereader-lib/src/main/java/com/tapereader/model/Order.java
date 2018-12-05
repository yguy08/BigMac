package com.tapereader.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tapereader.enumeration.Side;

@Entity
@Table(name = "ORDERS")
public class Order {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SECURITY_ID")
    private Security security;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="TIP_ID")
    private Tip tip;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="BUCKETSHOP_ID")
    private BucketShop bucketShop;
    
    private Side side;
    
    private BigDecimal quantity;

    public Order() {
        
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Tip getTip() {
        return tip;
    }

    public void setTip(Tip tip) {
        this.tip = tip;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
