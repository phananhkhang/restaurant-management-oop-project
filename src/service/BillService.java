package service;

import model.Bill;
import model.enums.BillStatus;
import repository.BillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BillService - Quản lý hóa đơn
 */
public class BillService implements BaseService<Bill> {
    private final BillRepository repository;
    
    public BillService(String filePath) {
        this.repository = new BillRepository(filePath);
    }
    
    @Override
    public List<Bill> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<Bill> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(Bill bill) {
        if (bill == null) throw new IllegalArgumentException("Bill không hợp lệ");
        repository.upsert(bill);
    }
    
    @Override
    public void update(Bill bill) {
        if (bill == null) throw new IllegalArgumentException("Bill không hợp lệ");
        repository.upsert(bill);
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
    public List<Bill> findByStatus(BillStatus status) {
        return getAll().stream()
            .filter(b -> b.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    public List<Bill> getOpenBills() {
        return findByStatus(BillStatus.OPEN);
    }
    
    public List<Bill> getPaidBills() {
        return findByStatus(BillStatus.PAID);
    }
    
    public List<Bill> getUnpaidBills() {
        return findByStatus(BillStatus.UNPAID);
    }
    
    /**
     * Tìm hóa đơn đang mở (UNPAID, PENDING, OPEN) của một bàn cụ thể
     */
    public Optional<Bill> getOpenBillForTable(String tableId) {
        if (tableId == null || tableId.trim().isEmpty()) {
            return Optional.empty();
        }
        return getAll().stream()
            .filter(b -> tableId.equals(b.getTableId()))
            .filter(b -> b.getStatus() == BillStatus.UNPAID 
                      || b.getStatus() == BillStatus.PENDING 
                      || b.getStatus() == BillStatus.OPEN)
            .findFirst();
    }
    
    /**
     * Tìm tất cả hóa đơn của một bàn
     */
    public List<Bill> findByTable(String tableId) {
        if (tableId == null || tableId.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return getAll().stream()
            .filter(b -> tableId.equals(b.getTableId()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            return repository.deleteById(id);
        } catch (Exception e) {
            // nếu repository ném lỗi, log ngắn và trả về false
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            return false;
        }
    }
}

