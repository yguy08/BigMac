package com.tapereader.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Security {

    @Id
    @GeneratedValue
    private Long id;

    private String symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUCKETSHOP_ID")
    private BucketShop bucketShop;

    public Security() {

    }

    public Security(String symbol) {
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BucketShop getBucketShop() {
        return bucketShop;
    }

    public void setBucketShop(BucketShop bucketShop) {
        this.bucketShop = bucketShop;
    }

    @Override
    public String toString() {
        return symbol + ":" + bucketShop.getName();
    }

}
