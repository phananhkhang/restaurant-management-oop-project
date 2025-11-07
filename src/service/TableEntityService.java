package service;

import model.Table;
import model.enums.TableStatus;
import repository.TableRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
public class TableEntityService implements BaseService<Table> {
    private final TableRepository repository;
    
    public TableEntityService(String filePath) {
        this.repository = new TableRepository(filePath);
    }
    
    @Override
    public List<Table> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<Table> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(Table table) {
        if (table == null) throw new IllegalArgumentException("Table không hợp lệ");
        repository.upsert(table);
    }
    
    @Override
    public void update(Table table) {
        if (table == null) throw new IllegalArgumentException("Table không hợp lệ");
        repository.upsert(table);
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
    public List<Table> findByStatus(TableStatus status) {
        return getAll().stream()
            .filter(t -> t.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    
    public List<Table> getAvailableTables() {
        return findByStatus(TableStatus.AVAILABLE);
    }
    
    public List<Table> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAll();
        }
        String searchTerm = name.trim().toLowerCase();
        return getAll().stream()
            .filter(t -> t.getTableName().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
    
    // ===== BUSINESS LOGIC =====
    public void occupyTable(String tableId) {
        Table table = getById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("Table không tồn tại"));
        table.occupy();
        repository.upsert(table);
    }
    
    public void releaseTable(String tableId) {
        Table table = getById(tableId)
            .orElseThrow(() -> new IllegalArgumentException("Table không tồn tại"));
        table.release();
        repository.upsert(table);
    }
    // ===== THỐNG KÊ =====
    public void printReport() {
        System.out.println("\n=== THỐNG KÊ BÀN ===");
        System.out.printf("Tổng: %d bàn%n", count());
        System.out.printf("Trống: %d bàn%n", getAvailableTables().size());
        System.out.println("\nTrạng thái:");
        for (TableStatus status : TableStatus.values()) {
            long cnt = findByStatus(status).size();
            System.out.printf("- %s: %d bàn%n", status, cnt);
        }
    }
}

