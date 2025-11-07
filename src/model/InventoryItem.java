package model;

import model.common.CsvEntity;
import model.enums.InventoryStatus;
import model.enums.Unit;

// Vật tư/kho (nguyên liệu, bao bì...) dùng enums Unit + InventoryStatus
public class InventoryItem implements CsvEntity {
    // Mã hàng kho
    private String stockItemId;
    // Tên hàng
    private String name;
    // Đơn vị tính (enum)
    private Unit unit;
    // Tồn hiện tại
    private double quantityOnHand;
    // Trạng thái hàng (enum)
    private InventoryStatus status;

    public InventoryItem() {
        // mặc định đang sử dụng, đơn vị cái
        this.status = InventoryStatus.IN_STOCK;
        this.unit = Unit.CAI;
    }

    public InventoryItem(String stockItemId, String name, Unit unit, double quantityOnHand,
                         InventoryStatus status) {
        this.stockItemId = stockItemId; 
        this.name = name;
        this.unit = unit == null ? Unit.CAI : unit;
        this.quantityOnHand = Math.max(0, quantityOnHand);
        this.status = status == null ? InventoryStatus.IN_STOCK : status;
        updateStatusByQuantity(); // Tự động cập nhật trạng thái dựa trên số lượng
    }
    
    /**
     * Tự động cập nhật trạng thái dựa trên số lượng tồn kho
     * - = 0: OUT_OF_STOCK (Hết hàng)
     * - 1-20: LOW_STOCK (Gần hết)
     * - > 20: IN_STOCK (Đủ hàng)
     */
    public void updateStatusByQuantity() {
        // Chỉ tự động cập nhật nếu không phải INACTIVE
        if (this.status == InventoryStatus.INACTIVE) {
            return; // Giữ nguyên trạng thái INACTIVE
        }
        
        if (this.quantityOnHand == 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } else if (this.quantityOnHand <= 20) {
            this.status = InventoryStatus.LOW_STOCK;
        } else {
            this.status = InventoryStatus.IN_STOCK;
        }
    }

    // Nghiệp vụ kho
    public void increase(double qty) {
        // tăng tồn (không cho âm)
        this.quantityOnHand = Math.max(0, this.quantityOnHand + Math.max(0, qty));
        updateStatusByQuantity(); // Tự động cập nhật trạng thái
    }

    public void decrease(double qty) {
        // giảm tồn (không âm)
        this.quantityOnHand = Math.max(0, this.quantityOnHand - Math.max(0, qty));
        updateStatusByQuantity(); // Tự động cập nhật trạng thái
    }

    public void displayInfo() {
        System.out.printf("%-8s %-22s %-6s %-10.2f %-10s%n",
                stockItemId, name, unit.name(), quantityOnHand, status.name());
    }

    // Getter/Setter
    public String getStockItemId() { return stockItemId; }
    public void setStockItemId(String stockItemId) { this.stockItemId = stockItemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }

    public double getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(double quantityOnHand) { 
        this.quantityOnHand = Math.max(0, quantityOnHand);
        updateStatusByQuantity(); // Tự động cập nhật trạng thái
    }

    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }


    // Tương thích ngược với code cũ (active boolean)
    public boolean isActive() { return this.status != InventoryStatus.INACTIVE; }
    public void setActive(boolean active) { 
        if (!active) {
            this.status = InventoryStatus.INACTIVE;
        } else {
            updateStatusByQuantity(); // Tự động cập nhật dựa trên số lượng
        }
    }

    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
            "stockItemId", "name", "unit", "quantityOnHand",
            "status"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(stockItemId),
            s(name),
            s(unit),
            s(quantityOnHand),
            s(status)
        };
    }
    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}