package service;

import model.Staff;
import model.enums.Role;
import model.enums.StaffStatus;
import repository.StaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StaffService - Quản lý nhân viên
 */
public class StaffService implements BaseService<Staff> {
    private final StaffRepository repository;
    
    public StaffService(String filePath) {
        this.repository = new StaffRepository(filePath);
    }
    
    @Override
    public List<Staff> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<Staff> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(Staff staff) {
        if (staff == null || staff.getId() == null) {
            throw new IllegalArgumentException("Staff không hợp lệ");
        }
        repository.upsert(staff);
    }
    
    @Override
    public void update(Staff staff) {
        if (staff == null) throw new IllegalArgumentException("Staff không hợp lệ");
        repository.upsert(staff);
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
    public List<Staff> searchByName(String name) {
        String term = name.toLowerCase();
        return getAll().stream()
            .filter(s -> s.getFullName().toLowerCase().contains(term))
            .collect(Collectors.toList());
    }
    
    public List<Staff> searchByPhone(String phone) {
        String term = phone.trim();
        return getAll().stream()
            .filter(s -> s.getPhone() != null && s.getPhone().contains(term))
            .collect(Collectors.toList());
    }
    
    public List<Staff> searchByAddress(String address) {
        String term = address.toLowerCase();
        return getAll().stream()
            .filter(s -> s.getAddress() != null && s.getAddress().toLowerCase().contains(term))
            .collect(Collectors.toList());
    }
    
    public Optional<Staff> findByUsername(String username) {
        return getAll().stream()
            .filter(s -> username.equals(s.getUsername()))
            .findFirst();
    }
    
    public List<Staff> findByRole(Role role) {
        return getAll().stream()
            .filter(s -> s.getRole() == role)
            .collect(Collectors.toList());
    }
    
    public List<Staff> getActiveStaff() {
        return getAll().stream()
            .filter(s -> s.getStatus() == StaffStatus.ACTIVE)
            .collect(Collectors.toList());
    }
    
    // ===== BUSINESS LOGIC =====
    public boolean authenticate(String username, String password) {
        Optional<Staff> staff = findByUsername(username);
        return staff.isPresent() && staff.get().checkPassword(password);
    }
    
    public void changePassword(String staffId, String newPassword) {
        Staff s = getById(staffId)
            .orElseThrow(() -> new IllegalArgumentException("Staff không tồn tại"));
        s.setPassword(newPassword);
        repository.upsert(s);
    }
    
    // ===== THỐNG KÊ =====
    public double getTotalSalary() {
        return getActiveStaff().stream().mapToDouble(Staff::getSalary).sum();
    }
    
    public void printReport() {
        System.out.println("\n=== THỐNG KÊ NHÂN VIÊN ===");
        System.out.printf("Tổng: %d nhân viên%n", count());
        System.out.printf("Đang làm: %d%n", getActiveStaff().size());
        System.out.printf("Tổng lương: %,.0f VNĐ%n", getTotalSalary());
        System.out.println("\nPhân bổ theo vai trò:");
        for (Role role : Role.values()) {
            long cnt = findByRole(role).size();
            System.out.printf("- %s: %d người%n", role, cnt);
        }
    }
}

