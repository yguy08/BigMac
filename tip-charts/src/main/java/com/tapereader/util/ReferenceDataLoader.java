package com.tapereader.util;

import java.util.List;

import javax.persistence.EntityManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.clerk.JPAClerk;
import com.tapereader.config.BaseModule;
import com.tapereader.dao.LookupClerk;
import com.tapereader.enumeration.TickerType;
import com.tapereader.model.BucketShop;
import com.tapereader.model.Security;
import com.tapereader.reference.BinanceReferenceDataClerk;
import com.tapereader.reference.PoloniexReferenceDataClerk;
import com.tapereader.reference.ReferenceDataClerk;

public class ReferenceDataLoader {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BaseModule("util-application.properties"));
        JPAClerk jpaClerk = injector.getInstance(JPAClerk.class);
        jpaClerk.init();
        
        LookupClerk lookupClerk = injector.getInstance(LookupClerk.class);
        lookupClerk.init();
        
        EntityManager em = jpaClerk.getEntityManager();

        em.getTransaction().begin();
        em.createQuery("DELETE FROM Security").executeUpdate();
        em.getTransaction().commit();
        
        em.getTransaction().begin();
        em.createQuery("DELETE FROM BucketShop").executeUpdate();
        em.getTransaction().commit();

        ReferenceDataClerk clerk = new PoloniexReferenceDataClerk();
        clerk.init();
        
        List<Security> poloSecurities = clerk.getSecurities();

        clerk = new BinanceReferenceDataClerk();
        clerk.init();
        
        List<Security> binanceSecurities = clerk.getSecurities();
        
        em.getTransaction().begin();
        
        try {
            BucketShop polo = new BucketShop(TickerType.POLONIEX.toString());
            em.persist(polo);
            for (Security s : poloSecurities) {
                s.setBucketShop(polo);
                em.persist(s);
            }
        } catch (Exception e) {
            
        }
        
        try {
            BucketShop binance = new BucketShop(TickerType.BINANCE.toString());
            em.persist(binance);
            for (Security s : binanceSecurities) {
                s.setBucketShop(binance);
                em.persist(s);
            }
        } catch (Exception e) {
            
        }

        em.getTransaction().commit();

        List<Security> allSecurities = em.createQuery("SELECT s FROM Security s").getResultList();

        for (Security s : allSecurities) {
            System.out.println(s.getId() + ", " + s.getSymbol() + ", " + s.getBucketShop());
        }

        em.close();
        jpaClerk.terminate();
    }

}
