package service;

import model.Expense;
import model.enums.ExpenseStatus;
import model.enums.ExpenseType;
import repository.ExpenseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ExpenseService - Quản lý chi phí
 */
public class ExpenseService implements BaseService<Expense> {
    private final ExpenseRepository repository;
    
    public ExpenseService(String filePath) {
        this.repository = new ExpenseRepository(filePath);
    }
    
    @Override
    public List<Expense> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<Expense> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(Expense expense) {
        if (expense == null) throw new IllegalArgumentException("Expense không hợp lệ");
        repository.upsert(expense);
    }
    
    @Override
    public void update(Expense expense) {
        if (expense == null) throw new IllegalArgumentException("Expense không hợp lệ");
        repository.upsert(expense);
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
    public List<Expense> findByType(ExpenseType type) {
        return getAll().stream()
            .filter(e -> e.getExpenseType() == type)
            .collect(Collectors.toList());
    }
    
    public List<Expense> findByStatus(ExpenseStatus status) {
        return getAll().stream()
            .filter(e -> e.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    public List<Expense> findByDate(String date) {
        return getAll().stream()
            .filter(e -> date.equals(e.getExpenseDate()))
            .collect(Collectors.toList());
    }
    
    public List<Expense> getPendingExpenses() {
        return findByStatus(ExpenseStatus.PENDING);
    }
    
    public List<Expense> getApprovedExpenses() {
        return findByStatus(ExpenseStatus.APPROVED);
    }
    
    // ===== BUSINESS LOGIC =====
    public void approveExpense(String expenseId, String approver) {
        Expense e = getById(expenseId)
            .orElseThrow(() -> new IllegalArgumentException("Expense không tồn tại"));
        e.approve(approver);
        repository.upsert(e);
    }
    
    public void rejectExpense(String expenseId, String reason) {
        Expense e = getById(expenseId)
            .orElseThrow(() -> new IllegalArgumentException("Expense không tồn tại"));
        e.reject(reason);
        repository.upsert(e);
    }
    
    public void markAsPaid(String expenseId) {
        Expense e = getById(expenseId)
            .orElseThrow(() -> new IllegalArgumentException("Expense không tồn tại"));
        e.markAsPaid();
        repository.upsert(e);
    }
    
    // ===== THỐNG KÊ =====
    public double getTotalExpense() {
        return getAll().stream()
            .filter(e -> e.getStatus() == ExpenseStatus.PAID)
            .mapToDouble(Expense::getAmount)
            .sum();
    }
    
    public double getTotalPendingExpense() {
        return getPendingExpenses().stream()
            .mapToDouble(Expense::getAmount)
            .sum();
    }
    
    public double getExpenseByType(ExpenseType type) {
        return findByType(type).stream()
            .filter(e -> e.getStatus() == ExpenseStatus.PAID)
            .mapToDouble(Expense::getAmount)
            .sum();
    }
    
    public void printReport() {
        System.out.println("\n=== THỐNG KÊ CHI PHÍ ===");
        System.out.printf("Tổng chi phí đã trả: %,.0f VNĐ%n", getTotalExpense());
        System.out.printf("Chi phí chờ duyệt: %,.0f VNĐ%n", getTotalPendingExpense());
        System.out.printf("Số khoản chi: %d%n", count());
        
        System.out.println("\nPhân bổ theo trạng thái:");
        System.out.println("(PENDING: Chờ duyệt | APPROVED: Đã duyệt | REJECTED: Từ chối | PAID: Đã thanh toán)");
        for (ExpenseStatus status : ExpenseStatus.values()) {
            long cnt = findByStatus(status).size();
            double total = findByStatus(status).stream()
                .mapToDouble(Expense::getAmount).sum();
            System.out.printf("- %s: %d khoản (%,.0f VNĐ)%n", status, cnt, total);
        }
        
        System.out.println("\nPhân bổ theo loại chi phí:");
        for (ExpenseType type : ExpenseType.values()) {
            double total = getExpenseByType(type);
            if (total > 0) {
                System.out.printf("- %s: %,.0f VNĐ%n", type, total);
            }
        }
    }
}

