package com.tapereader.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DopeBook {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="TIP_ID")
    private Tip tip;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SECURITY_ID")
    private Security security;
    
    public DopeBook() {
        
    }

    public Tip getTip() {
        return tip;
    }

    public void setTip(Tip tip) {
        this.tip = tip;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }
}
