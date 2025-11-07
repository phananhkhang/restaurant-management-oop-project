package model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.common.CsvEntity;
import model.common.Money;
import model.enums.BillStatus;

public class Bill implements CsvEntity {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private String id;
    private LocalDateTime createdAt, updatedAt, closedAt;
    private String tableId;     // Mã bàn liên kết với hóa đơn
    private List<String> items; // Danh sách món ăn
    private BillStatus status;
    private String note;

    private Money subTotal, discountAmount, totalAmount, paidAmount;

    public Bill() {
        // Không tự sinh ID ở đây, để BillService sinh ID
        this.id = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.items = new ArrayList<>();
        this.status = BillStatus.PENDING; // Khởi tạo trạng thái chờ xử lý.
        this.subTotal = new Money(BigDecimal.ZERO);
        this.discountAmount = new Money(BigDecimal.ZERO);
        this.totalAmount = new Money(BigDecimal.ZERO);
        this.paidAmount = new Money(BigDecimal.ZERO);
    }

    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(String item) {
        if (item != null && !item.isEmpty()) {
            this.items.add(item);
            touch();
        }
    }
    
    public void removeItem(String item) {
        if (item != null) {
            this.items.remove(item);
            touch();
        }
    }
    
    public void removeItemAt(int index) {
        if (index >= 0 && index < this.items.size()) {
            this.items.remove(index);
            touch();
        }
    }

    /** Cập nhật trạng thái Bill dựa vào số tiền đã thanh toán và tổng tiền */
    public void updateStatusByPaidAmount() {
        if (this.totalAmount == null || this.paidAmount == null) return;
        // Nếu bill bị hủy, trạng thái luôn CANCELLED
        if (this.status == BillStatus.CANCELLED) {
            this.closedAt = LocalDateTime.now();
            return;
        }
        // Bill chưa thanh toán
        if (this.paidAmount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            this.status = BillStatus.UNPAID;
            this.closedAt = null;
            return;
        }
        // Bill đã thanh toán đủ
        int cmp = this.paidAmount.getAmount().compareTo(this.totalAmount.getAmount());
        if (cmp >= 0 && this.totalAmount.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            this.status = BillStatus.PAID;
            this.closedAt = LocalDateTime.now();
        } else {
            // Nếu chưa thanh toán đủ nhưng đã trả một phần thì vẫn coi là UNPAID (do không có PARTIALLY_PAID)
            this.status = BillStatus.UNPAID;
            this.closedAt = null;
        }
    }
    /** Tính lại tổng tiền: subTotal - discountAmount */
    public void recomputeTotal() {
        this.totalAmount = new Money(
            this.subTotal.getAmount()
                .subtract(this.discountAmount.getAmount())
        );
        updateStatusByPaidAmount();
        touch();
    }
    // ===== Getter/Setter =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; touch(); }
    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; touch(); }
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; touch(); }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; touch(); }
    public Money getSubTotal() { return subTotal; }
    public void setSubTotal(Money subTotal) { this.subTotal = subTotal; touch(); }
    public Money getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Money discountAmount) { this.discountAmount = discountAmount; touch(); }
    public Money getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Money totalAmount) { this.totalAmount = totalAmount; updateStatusByPaidAmount(); touch(); }
    public Money getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Money paidAmount) { this.paidAmount = paidAmount; updateStatusByPaidAmount(); touch(); }
    // ===== CsvEntity =====
    @Override
    public String[] csvHeader() {
        return new String[] {
            "id", "createdAt", "updatedAt", "closedAt",
            "tableId", "status", "note",
            "subTotal", "discountAmount", "totalAmount", "paidAmount",
            "items"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            nullSafe(id),
            formatDateTime(createdAt),
            formatDateTime(updatedAt),
            formatDateTime(closedAt),
            nullSafe(tableId),
            nullSafe(status),
            nullSafe(note),
            nullSafe(subTotal),
            nullSafe(discountAmount),
            nullSafe(totalAmount),
            nullSafe(paidAmount),
            items.isEmpty() ? "" : String.join("|", items)
        };
    }
    
    private static String formatDateTime(LocalDateTime dt) {
        return (dt == null) ? "" : dt.format(FORMATTER);
    }
    
    private static String nullSafe(Object o) { return (o == null) ? "" : o.toString(); }
}
