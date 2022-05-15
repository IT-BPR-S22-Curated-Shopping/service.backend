package bpr.service.backend.services.data;

import java.util.List;

public interface ICRUDService<T> {
    List<T> readAll();
    T readById(Long id);
    T create(T entity);
    T update(Long id, T entity);
    void delete(Long id);
}
