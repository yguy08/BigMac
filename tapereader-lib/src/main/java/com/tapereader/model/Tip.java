package com.tapereader.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Tip {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy="tip", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Line> lines;
    
    @OneToMany(mappedBy="tip", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Order> orders;
    
    @OneToMany(mappedBy="tip", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<DopeBook> dopeBooks;
    
    @OneToMany(mappedBy="tip", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Roll> rolls;
    
    public Tip() {
        
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<DopeBook> getDopeBooks() {
        return dopeBooks;
    }

    public void setDopeBooks(List<DopeBook> dopeBooks) {
        this.dopeBooks = dopeBooks;
    }

    public List<Roll> getRolls() {
        return rolls;
    }

    public void setRolls(List<Roll> rolls) {
        this.rolls = rolls;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
