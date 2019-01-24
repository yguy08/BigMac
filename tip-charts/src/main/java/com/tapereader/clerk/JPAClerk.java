package com.tapereader.clerk;

import javax.persistence.EntityManager;

public interface JPAClerk extends Clerk {

    public EntityManager getEntityManager();

}
