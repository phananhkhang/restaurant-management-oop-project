package service;

import model.MenuItem;
import model.enums.MenuType;
import repository.MenuItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MenuItemService - Quản lý thực đơn
 */
public class MenuItemService implements BaseService<MenuItem> {
    private final MenuItemRepository repository;
    
    public MenuItemService(String filePath) {
        this.repository = new MenuItemRepository(filePath);
    }
    
    @Override
    public List<MenuItem> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<MenuItem> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(MenuItem item) {
        if (item == null || item.getItemId() == null) {
            throw new IllegalArgumentException("MenuItem không hợp lệ");
        }
        repository.upsert(item);
    }
    
    @Override
    public void update(MenuItem item) {
        if (item == null) throw new IllegalArgumentException("MenuItem không hợp lệ");
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
    public List<MenuItem> searchByName(String name) {
        String term = name.toLowerCase();
        return getAll().stream()
            .filter(m -> m.getItemName().toLowerCase().contains(term))
            .collect(Collectors.toList());
    }
    
    public List<MenuItem> findByCategory(MenuType category) {
        return getAll().stream()
            .filter(m -> m.getCategory() == category)
            .collect(Collectors.toList());
    }
    
    public List<MenuItem> getAvailableItems() {
        return getAll().stream()
            .filter(MenuItem::isAvailable)
            .collect(Collectors.toList());
    }
    
    public List<MenuItem> findByPriceRange(double min, double max) {
        return getAll().stream()
            .filter(m -> m.getPrice() >= min && m.getPrice() <= max)
            .collect(Collectors.toList());
    }
    
    // ===== BUSINESS LOGIC =====
    public void setAvailability(String itemId, boolean available) {
        MenuItem m = getById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("MenuItem không tồn tại"));
        m.setAvailable(available);
        repository.upsert(m);
    }
    
    public void updatePrice(String itemId, double newPrice) {
        MenuItem m = getById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("MenuItem không tồn tại"));
        m.setPrice(newPrice);
        repository.upsert(m);
    }
    
    // ===== THỐNG KÊ =====
    public double getAveragePrice() {
        return getAll().stream().mapToDouble(MenuItem::getPrice).average().orElse(0.0);
    }
    
    public List<MenuItem> getMostExpensive(int limit) {
        return getAll().stream()
            .sorted((m1, m2) -> Double.compare(m2.getPrice(), m1.getPrice()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public void printReport() {
        System.out.println("\n=== THỐNG KÊ THỰC ĐƠN ===");
        System.out.printf("Tổng món: %d%n", count());
        System.out.printf("Món khả dụng: %d%n", getAvailableItems().size());
        System.out.printf("Giá trung bình: %,.0f VNĐ%n", getAveragePrice());
        System.out.println("\nPhân bổ theo danh mục:");
        for (MenuType cat : MenuType.values()) {
            long cnt = findByCategory(cat).size();
            System.out.printf("- %s: %d món%n", cat, cnt);
        }
    }
}

