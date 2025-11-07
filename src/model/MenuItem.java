package model;

import model.common.CsvEntity;
import model.enums.MenuType;
// Món trong menu dùng enum MenuCategory
public class MenuItem implements CsvEntity {
    private String itemId;           // Mã món
    private String itemName;         // Tên món
    private MenuType category;   // Danh mục
    private double price;            // Giá bán
    private String description;      // Mô tả
    private boolean available;       // Còn bán?
    private int preparationTime;     // Phút chế biến
    public MenuItem() {}

    public MenuItem(String itemId, String itemName, MenuType category, double price,
                    String description, boolean available, int preparationTime) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.available = available;
        this.preparationTime = Math.max(0, preparationTime);
    }

    // Getter/Setter
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public MenuType getCategory() { return category; }
    public void setCategory(MenuType category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = Math.max(0, preparationTime); }

    public void displayInfo() {
        String avail = available ? "Còn" : "Hết";
        System.out.printf("%-8s %-28s %-12s %-12s %-4s %3d'%n",
                itemId, itemName, category.name(), String.format("%,.0f", price), avail, preparationTime);
    }

    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
            "itemId", "itemName", "category", "price",
            "description", "available", "preparationTime"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(itemId),
            s(itemName),
            s(category),
            s(price),
            s(description),
            s(available),
            s(preparationTime)
        };
    }
    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}