package model;

import model.common.CsvEntity;
import model.enums.ExpenseStatus;
import model.enums.ExpenseType;

// Chi phí vận hành dùng enum ExpenseType + ExpenseStatus
// Chỉ có quản lý mới được duyệt, từ chối chi phí, còn nhân viên thì chỉ có thể thêm chi phí để duyệt thôi
public class Expense implements CsvEntity {
    private String expenseId;        // Mã chi phí
    private ExpenseType expenseType; // Loại chi phí
    private double amount;           // Số tiền
    private String expenseDate;      // yyyy-MM-dd
    private String description;      // Mô tả
    private String approvedBy;       // Người duyệt
    private ExpenseStatus status;    // Trạng thái
    private String receipt;          // Số biên lai
    private String notes;            // Ghi chú

    public Expense() {
        this.status = ExpenseStatus.PENDING;
        this.expenseType = ExpenseType.OTHER;
    }

    public Expense(String expenseId, ExpenseType expenseType, double amount, String expenseDate, String description) {
        this.expenseId = expenseId;
        this.expenseType = expenseType;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
        this.status = ExpenseStatus.PENDING;
    }

    // Duyệt chi
    public void approve(String approver) {
        this.approvedBy = approver;
        this.status = ExpenseStatus.APPROVED;
    }

    // Từ chối
    public void reject(String reason) {
        this.status = ExpenseStatus.REJECTED;
        this.notes = reason;
    }

    // Đã chi
    public void markAsPaid() {
        this.status = ExpenseStatus.PAID;
    }

    public void displayInfo() {
        System.out.printf("%-8s %-12s %-12s %-10s %-24s%n",
                expenseId, expenseType.name(), String.format("%,.0f", amount), expenseDate, status.name());
    }

    // Getter/Setter
    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public ExpenseType getExpenseType() { return expenseType; }
    public void setExpenseType(ExpenseType expenseType) { this.expenseType = expenseType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getExpenseDate() { return expenseDate; }
    public void setExpenseDate(String expenseDate) { this.expenseDate = expenseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public ExpenseStatus getStatus() { return status; }
    public void setStatus(ExpenseStatus status) { this.status = status; }

    public String getReceipt() { return receipt; }
    public void setReceipt(String receipt) { this.receipt = receipt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
            "expenseId", "expenseType", "amount", "expenseDate",
            "description", "approvedBy", "status", "receipt", "notes"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(expenseId),
            s(expenseType),
            s(amount),
            s(expenseDate),
            s(description),
            s(approvedBy),
            s(status),
            s(receipt),
            s(notes)
        };
    }

    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }

}