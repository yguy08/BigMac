package com.tapereader.clerk;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.tapereader.marketdata.Bar;
import com.tapereader.marketdata.Tick;
import com.tapereader.model.Line;
import com.tapereader.model.Security;
import com.tapereader.util.TradingUtils;

public class RecordClerkImpl implements RecordClerk {

    private EntityManager em;
    
    private JPAClerk jpaClerk;

    @Inject(optional = true)
    @Named("cleanmarketdata")
    private String cleanMarketData;
    
    @Inject
    private RecordClerkImpl(Provider<JPAClerk> jpaClerk) {
        this.jpaClerk = jpaClerk.get();
    }

    @Override
    public void updateBars(Security security, List<Bar> bars) {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM MarketData " + "WHERE type = Bar " + "AND symbol like :symbol")
                .setParameter("symbol", TradingUtils.toSymbol(security)).executeUpdate();

        for (Bar b : bars) {
            em.persist(b);
        }
        em.getTransaction().commit();
    }

    @Override
    public void updateLine(Line line) {

    }

    @Override
    public void init() {
        em = jpaClerk.getEntityManager();
        cleanMarketData();
    }

    private void cleanMarketData() {
        if (Boolean.valueOf(cleanMarketData)) {
            em.getTransaction().begin();
            int del = em.createNamedQuery("MarketData.clean").executeUpdate();
            em.getTransaction().commit();
            System.out.println(del);
        }
    }

    @Override
    public void terminate() {
        jpaClerk.terminate();
    }

    @Override
    public void persist(Object object) {
        em.getTransaction().begin();
        em.persist(object);
        em.getTransaction().commit();
    }

    @Override
    public void saveTicks(List<Tick> ticks) {
        em.getTransaction().begin();
        try {
            for (int i = 0; i < ticks.size(); i++) {
                em.persist(ticks.get(i));
                if( i % 50 == 0 ) { 
                    em.flush();
                    em.clear();
                 }
            }
        em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
