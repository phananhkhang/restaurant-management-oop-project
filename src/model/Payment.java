package model;

import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import model.common.CsvEntity;

// Thanh toán dùng enum PaymentMethod + PaymentStatus
public class Payment implements CsvEntity {
    private String paymentId;         // Mã thanh toán
    private String orderId;           // Mã đơn
    private PaymentMethod paymentMethod; // Phương thức
    private double amount;            // Số tiền
    private String paymentDate;       // yyyy-MM-dd
    private String paymentTime;       // HH:mm
    private PaymentStatus status;     // Trạng thái
    private String transactionId;     // Mã giao dịch
    private String notes;             // Ghi chú

    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.paymentMethod = PaymentMethod.CASH;
    }

    public Payment(String paymentId, String orderId, PaymentMethod paymentMethod,
                   double amount, String paymentDate, String paymentTime) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentTime = paymentTime;
        this.status = PaymentStatus.PENDING;
    }

    // Hoàn tất thanh toán
    public void complete(String txnId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = txnId;
    }

    // Hoàn tiền
    public void refund(String note) {
        this.status = PaymentStatus.REFUNDED;
        this.notes = note;
    }

    public void displayInfo() {
        System.out.printf("%-10s %-8s %-10s %-12s %-10s %-16s%n",
                paymentId, orderId, paymentMethod.name(), String.format("%,.0f", amount),
                status.name(), transactionId == null ? "-" : transactionId);
    }

    // Getter/Setter
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentTime() { return paymentTime; }
    public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }


    @Override
    public String[] csvHeader() {
        return new String[] {
            "paymentId", "orderId", "paymentMethod", "amount",
            "paymentDate", "paymentTime", "status", "transactionId", "notes"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(paymentId),
            s(orderId),
            s(paymentMethod),
            String.valueOf(amount),
            s(paymentDate),
            s(paymentTime),
            s(status),
            s(transactionId),
            s(notes)
        };
    }
    
    private String s(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}