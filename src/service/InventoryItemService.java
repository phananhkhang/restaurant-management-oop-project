package service;

import model.InventoryItem;
import model.enums.InventoryStatus;
import repository.InventoryItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * InventoryItemService - Quản lý kho nguyên liệu
 */
public class InventoryItemService implements BaseService<InventoryItem> {
    private final InventoryItemRepository repository;
    
    public InventoryItemService(String filePath) {
        this.repository = new InventoryItemRepository(filePath);
    }
    
    @Override
    public List<InventoryItem> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<InventoryItem> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(InventoryItem item) {
        if (item == null || item.getStockItemId() == null) {
            throw new IllegalArgumentException("InventoryItem không hợp lệ");
        }
        repository.upsert(item);
    }
    
    @Override
    public void update(InventoryItem item) {
        if (item == null) throw new IllegalArgumentException("InventoryItem không hợp lệ");
        repository.upsert(item);
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
    public List<InventoryItem> searchByName(String name) {
        String term = name.toLowerCase();
        return getAll().stream()
            .filter(i -> i.getName().toLowerCase().contains(term))
            .collect(Collectors.toList());
    }
    
    public List<InventoryItem> getActiveItems() {
        return getAll().stream()
            .filter(i -> i.getStatus() != InventoryStatus.INACTIVE)
            .collect(Collectors.toList());
    }
    
    
    // ===== BUSINESS LOGIC =====
    public void increaseStock(String itemId, double quantity) {
        InventoryItem item = getById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item không tồn tại"));
        item.increase(quantity);
        repository.upsert(item);
    }
    
    public void decreaseStock(String itemId, double quantity) {
        InventoryItem item = getById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item không tồn tại"));
        item.decrease(quantity);
        repository.upsert(item);
    }
    
    public void adjustStock(String itemId, double newQuantity) {
        InventoryItem item = getById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item không tồn tại"));
        item.setQuantityOnHand(newQuantity);
        repository.upsert(item);
    }
    
    // ===== THỐNG KÊ =====
   public void printReport() {
        List<InventoryItem> items = getAll();
        
        // Đếm theo trạng thái
        long outOfStock = items.stream().filter(i -> i.getStatus() == InventoryStatus.OUT_OF_STOCK).count();
        long lowStock = items.stream().filter(i -> i.getStatus() == InventoryStatus.LOW_STOCK).count();
        long inStock = items.stream().filter(i -> i.getStatus() == InventoryStatus.IN_STOCK).count();
        long inactive = items.stream().filter(i -> i.getStatus() == InventoryStatus.INACTIVE).count();
        
        System.out.println("\n=== THỐNG KÊ TỒN KHO ===");
        System.out.printf("Tổng mặt hàng: %d%n", count());
        System.out.printf("Đang hoạt động: %d%n", getActiveItems().size());
        System.out.println("-------------------------------");
        System.out.printf("✅ Đủ hàng (IN_STOCK):        %d mặt hàng%n", inStock);
        System.out.printf("⚠️  Gần hết (LOW_STOCK):       %d mặt hàng%n", lowStock);
        System.out.printf("❌ Hết hàng (OUT_OF_STOCK):   %d mặt hàng%n", outOfStock);
        System.out.printf("🚫 Ngừng dùng (INACTIVE):     %d mặt hàng%n", inactive);
        System.out.println("-------------------------------");

        if (items.isEmpty()) {
            System.out.println("Không có mặt hàng trong kho.");
            return;
        }

        System.out.println("\n🔎 CHI TIẾT TỒN KHO (Số lượng theo từng mặt hàng)");
        System.out.println(repeat("-", 100));
        System.out.printf("%-10s %-30s %-13s %-15s %-15s%n",
            "Mã", "Tên", "Đơn vị", "Tồn kho", "Trạng thái");
        System.out.println(repeat("-", 100));

        for (InventoryItem item : items) {
            System.out.printf("%-10s %-30s %-13s %-15.2f %-15s%n",
                item.getStockItemId(),
                item.getName(),
                item.getUnit(),
                item.getQuantityOnHand(),
                item.getStatus());
        }
        System.out.println(repeat("-", 100));
    }

   private String repeat(String s, int count) {
        if (s == null || count <= 0) return "";
        StringBuilder sb = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }
}

