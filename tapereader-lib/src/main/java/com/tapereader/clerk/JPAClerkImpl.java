package com.tapereader.clerk;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum JPAClerkImpl implements JPAClerk {

    INSTANCE;

    private EntityManagerFactory emFactory;

    @Override
    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    @Override
    public void init() {
        this.emFactory = Persistence.createEntityManagerFactory("entity");
    }

    @Override
    public void terminate() {
        if (emFactory.isOpen()) {
            emFactory.close();
        }
    }

}