package com.tapereader.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class BucketShop {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "bucketShop", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Account> accounts;

    @OneToMany(mappedBy = "bucketShop", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Security> securities;

    public BucketShop() {

    }

    public BucketShop(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public void setSecurities(List<Security> securities) {
        this.securities = securities;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
