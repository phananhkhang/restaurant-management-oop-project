package service;

import java.util.List;
import java.util.Optional;

/**
 * Interface chung cho tất cả Service
 * Định nghĩa các phương thức CRUD cơ bản
 */
public interface BaseService<T> {
    // ===== CRUD Cơ bản =====
    List<T> getAll();
    Optional<T> getById(String id);
    void create(T entity);
    void update(T entity);
    boolean delete(String id);
    
    // ===== Persistence =====
    void loadData() throws Exception;
    void saveData() throws Exception;
    
    // ===== Thống kê cơ bản =====
    int count();
}

