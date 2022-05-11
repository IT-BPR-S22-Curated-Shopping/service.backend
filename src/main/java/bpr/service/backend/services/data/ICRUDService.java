package bpr.service.backend.services.data;

import java.util.List;

public interface ICRUDService<T> {
    List<T> ReadAll();
    T ReadById(Long id);
    T Create(T entity);
    T Update(Long id, T entity);
    void Delete(Long id);
}
