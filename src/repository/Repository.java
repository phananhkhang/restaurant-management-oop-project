package repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    List<T> findAll();
    Optional<T> findById(String id);
    void upsert(T entity);
    void upsertAll(List<T> entities);
    boolean deleteById(String id);
    void load() throws Exception;
    void persist() throws Exception;
}