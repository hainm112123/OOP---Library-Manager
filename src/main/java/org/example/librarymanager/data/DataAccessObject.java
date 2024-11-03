package org.example.librarymanager.data;

import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getAll();

    public T getById(int id);

    public T add(T entity);

    public boolean update(T entity);

    public boolean delete(T entity);
}
