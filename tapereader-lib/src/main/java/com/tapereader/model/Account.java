package com.tapereader.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy="account", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Ticket> tickets;

    @OneToMany(mappedBy="account", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Order> orders;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "BUCKETSHOP_ID")
    private BucketShop bucketShop;

    public Account() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public BucketShop getBucketShop() {
        return bucketShop;
    }

    public void setBucketShop(BucketShop bucketShop) {
        this.bucketShop = bucketShop;
    }

}
