package service;

import model.Payment;
import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import repository.PaymentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PaymentService - Quản lý thanh toán
 */
public class PaymentService implements BaseService<Payment> {
    private final PaymentRepository repository;
    
    public PaymentService(String filePath) {
        this.repository = new PaymentRepository(filePath);
    }
    
    @Override
    public List<Payment> getAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<Payment> getById(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void create(Payment payment) {
        if (payment == null) throw new IllegalArgumentException("Payment không hợp lệ");
        repository.upsert(payment);
    }
    
    @Override
    public void update(Payment payment) {
        if (payment == null) throw new IllegalArgumentException("Payment không hợp lệ");
        repository.upsert(payment);
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
    public List<Payment> findByOrderId(String orderId) {
        return getAll().stream()
            .filter(p -> orderId.equals(p.getOrderId()))
            .collect(Collectors.toList());
    }
    
    public List<Payment> findByMethod(PaymentMethod method) {
        return getAll().stream()
            .filter(p -> p.getPaymentMethod() == method)
            .collect(Collectors.toList());
    }
    
    public List<Payment> findByStatus(PaymentStatus status) {
        return getAll().stream()
            .filter(p -> p.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    public List<Payment> findByDate(String date) {
        return getAll().stream()
            .filter(p -> date.equals(p.getPaymentDate()))
            .collect(Collectors.toList());
    }
    
    // ===== BUSINESS LOGIC =====
    public void confirmPayment(String paymentId) {
        Payment payment = getById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));
        payment.setStatus(PaymentStatus.COMPLETED);
        repository.upsert(payment);
    }
    
    public void failPayment(String paymentId) {
        Payment payment = getById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));
        payment.setStatus(PaymentStatus.FAILED);
        repository.upsert(payment);
    }
    
    public void refundPayment(String paymentId) {
        Payment payment = getById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));
        payment.setStatus(PaymentStatus.REFUNDED);
        repository.upsert(payment);
    }
    
    // ===== THỐNG KÊ =====
    public double getTotalAmount() {
        return getAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    public double getTodayAmount() {
        String today = LocalDate.now().toString();
        return findByDate(today).stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    public void printReport() {
        System.out.println("\n=== THỐNG KÊ THANH TOÁN ===");
        System.out.printf("Tổng giao dịch: %d%n", count());
        System.out.printf("Tổng tiền: %,.0f VNĐ%n", getTotalAmount());
        System.out.printf("Hôm nay: %,.0f VNĐ%n", getTodayAmount());
        System.out.println("\nPhương thức:");
        for (PaymentMethod method : PaymentMethod.values()) {
            double total = findByMethod(method).stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount).sum();
            System.out.printf("- %s: %,.0f VNĐ%n", method, total);
        }
    }
}

