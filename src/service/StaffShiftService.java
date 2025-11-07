package service;

import model.StaffShift;
import model.enums.StaffShiftStatus;
import repository.StaffShiftRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StaffShiftService - Quản lý phân ca nhân viên
 */
public class StaffShiftService implements BaseService<StaffShift> {
    private final StaffShiftRepository repository;
    
    public StaffShiftService(String filePath) {
        this.repository = new StaffShiftRepository(filePath);
    }
    
    @Override
    public List<StaffShift> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<StaffShift> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(StaffShift staffShift) {
        if (staffShift == null) throw new IllegalArgumentException("StaffShift không hợp lệ");
        repository.upsert(staffShift);
    }
    
    @Override
    public void update(StaffShift staffShift) {
        if (staffShift == null) throw new IllegalArgumentException("StaffShift không hợp lệ");
        repository.upsert(staffShift);
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
    public List<StaffShift> findByStaffId(String staffId) {
        return getAll().stream()
            .filter(ss -> staffId.equals(ss.getStaffId()))
            .collect(Collectors.toList());
    }
    
    public List<StaffShift> findByDate(String date) {
        return getAll().stream()
            .filter(ss -> date.equals(ss.getWorkDate()))
            .collect(Collectors.toList());
    }
    
    public List<StaffShift> findByStatus(StaffShiftStatus status) {
        return getAll().stream()
            .filter(ss -> ss.getStatus() == status)
            .collect(Collectors.toList());
    }
}

