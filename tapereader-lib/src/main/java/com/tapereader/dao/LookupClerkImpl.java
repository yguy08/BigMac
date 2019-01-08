package com.tapereader.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.enumeration.TickerType;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.MarketData;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Line;
import com.tapereader.model.Security;
import com.tapereader.model.Tip;
import com.tapereader.util.TradingUtils;

public class LookupClerkImpl implements LookupClerk {
    
    private EntityManager em;
    
    private JPAClerk jpaClerk;
    
    @Inject
    private LookupClerkImpl(Provider<JPAClerk> jpaClerk) {
        this.jpaClerk = jpaClerk.get();
    }

    @Override
    public Security findSecurity(String symbol, TickerType tickerType) {
        BucketShop shop = (BucketShop) em.createQuery("SELECT b FROM BucketShop b WHERE name like :name")
                .setParameter("name", tickerType.toString())
                .getSingleResult();
        Security security = (Security) em.createQuery("SELECT s FROM Security s WHERE symbol like :symbol AND bucketshop_id like :id")
                .setParameter("symbol", symbol)
                .setParameter("id", shop.getId())
                .getSingleResult();
        return security;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Tick> getCurrentTicks() {
        List<Tick> ticks = em.createNamedQuery("Tick.findLatest").getResultList();
        System.out.println(ticks.size());
        return ticks;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Bar> getBars(Security security) {
        List<Bar> bars = em.createQuery("SELECT b FROM MarketData b WHERE TYPE(b) = Bar AND symbol like :symbol "
                + "ORDER BY timestamp ASC")
                .setParameter("symbol", TradingUtils.toSymbol(security))
                .getResultList();
        return !TradingUtils.isEmpty(bars) ? bars : null;
    }
    


    @SuppressWarnings("unchecked")
    @Override
    public List<BucketShop> getAllBucketShops() {
        List<BucketShop> shops = em.createQuery("SELECT b FROM BucketShop b")
                .getResultList();
        return shops;
    }

    @Override
    public void init() {
        em = jpaClerk.getEntityManager();
    }

    @Override
    public void terminate() {
        jpaClerk.terminate();
    }

    @Override
    public Tip findTipByName(String tipName) {
        try {
            Tip tip = (Tip) em.createQuery("SELECT t FROM Tip t WHERE name like :name")
                    .setParameter("name", tipName)
                    .getSingleResult();
            return tip;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Tip> getAllTips() {
        List<Tip> tips = em.createQuery("SELECT t FROM Tip t")
                .getResultList();
        return tips;
    }

    @Override
    public BucketShop getBucketShop(TickerType tickerType) {
        BucketShop shop = (BucketShop) em.createQuery("SELECT b FROM BucketShop b WHERE name like :name")
                .setParameter("name", tickerType.toString())
                .getSingleResult();
        return shop;
    }

}
