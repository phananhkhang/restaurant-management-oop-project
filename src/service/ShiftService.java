package service;

import model.Shift;
import model.enums.ShiftType;
import repository.ShiftRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ShiftService - Quản lý ca làm việc
 */
public class ShiftService implements BaseService<Shift> {
    private final ShiftRepository repository;
    
    public ShiftService(String filePath) {
        this.repository = new ShiftRepository(filePath);
    }
    
    @Override
    public List<Shift> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<Shift> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(Shift shift) {
        if (shift == null) throw new IllegalArgumentException("Shift không hợp lệ");
        repository.upsert(shift);
    }
    
    @Override
    public void update(Shift shift) {
        if (shift == null) throw new IllegalArgumentException("Shift không hợp lệ");
        repository.upsert(shift);
    }
    
    @Override
    public boolean delete(String id) {
        return repository.deleteById(id);
    }
    
    @Override
    public void loadData() throws Exception {
        repository.load();
    }
    
    @Override
    public void saveData() throws Exception {
        repository.persist();
    }
    
    @Override
    public int count() {
        return getAll().size();
    }
    
    // ===== TÌM KIẾM =====
    public List<Shift> findByType(ShiftType type) {
        return getAll().stream()
            .filter(s -> s.getShiftType() == type)
            .collect(Collectors.toList());
    }
    
    public void printReport() {
        System.out.println("\n=== DANH SÁCH CA LÀM VIỆC ===");
        getAll().forEach(s -> 
            System.out.printf("%s: %s - %s (%s)%n", 
                s.getShiftId(), s.getStartTime(), s.getEndTime(), s.getShiftType())
        );
    }
}

