package model.enums;

public enum BillStatus {
    UNPAID,     // Chưa thanh toán
    PAID,       // Đã thanh toán
    CANCELLED,  // Đã hủy
    PENDING,    // Đang chờ xử lý
    OPEN;       // Mở, đang xử lý
}