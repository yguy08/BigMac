package com.bigmac.dao;

import java.util.Collection;
import java.util.List;

public interface IDao<T> {

    public boolean save(T obj) throws Exception;

    public boolean update(T obj) throws Exception;

    public boolean save(Collection<T> obj) throws Exception;

    public boolean delete(T obj) throws Exception;

    public List<T> loadAll() throws Exception;
}
