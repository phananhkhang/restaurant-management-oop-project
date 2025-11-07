package model.enums;

// Trạng thái tồn kho dựa trên số lượng
public enum InventoryStatus {
    OUT_OF_STOCK,    // Hết hàng (= 0)
    LOW_STOCK,       // Gần hết (1-20)
    IN_STOCK,        // Đủ hàng (> 20)
    INACTIVE         // Ngừng sử dụng
}