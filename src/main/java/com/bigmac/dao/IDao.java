package com.bigmac.dao;

import java.util.Collection;

public interface IDao<T> {

    public boolean save(T obj) throws Exception;

    public boolean update(T obj) throws Exception;

    public boolean save(Collection<T> obj) throws Exception;

    public boolean delete(T obj) throws Exception;

}
