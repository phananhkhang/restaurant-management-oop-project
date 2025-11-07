package service;

import model.enums.BillStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ReportService - Báo cáo tổng hợp
 * Tổng hợp dữ liệu từ nhiều service để tạo báo cáo đầy đủ
 */
public class ReportService {
    private final StaffService staffService;
    private final MenuItemService menuService;
    private final InventoryItemService inventoryService;
    private final PaymentService paymentService;
    private final ExpenseService expenseService;
    private final TableEntityService tableService;
    private final BillService billService;
    
    public ReportService(
        StaffService staffService,
        MenuItemService menuService,
        InventoryItemService inventoryService,
        PaymentService paymentService,
        ExpenseService expenseService,
        TableEntityService tableService,
        BillService billService
    ) {
        this.staffService = staffService;
        this.menuService = menuService;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        this.expenseService = expenseService;
        this.tableService = tableService;
        this.billService = billService;
    }
    
    /**
     * Báo cáo tổng quan hệ thống
     */
    public void printSystemOverview() {
        printSystemOverview(null, null);
    }
    
    public void printSystemOverview(LocalDate start, LocalDate end) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          THỐNG KÊ TỔNG QUAN HỆ THỐNG                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        if (start != null && end != null) {
            System.out.println("Khoảng: " + start + " -> " + end);
        } else {
            System.out.println("Thời gian: " + LocalDateTime.now());
        }
        System.out.println();
        
        // Dữ liệu cơ bản
        System.out.println("┌─── DỮ LIỆU CƠ BẢN ───────────────────────────────────────┐");
        System.out.printf("│ Nhân viên:       %5d                                     │%n", staffService.count());
        System.out.printf("│ Món ăn:          %5d                                     │%n", menuService.count());
        System.out.printf("│ Bàn:             %5d                                     │%n", tableService.count());
        System.out.printf("│ Nguyên liệu:     %5d                                     │%n", inventoryService.count());
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
        
        // Tính doanh thu từ Bill PAID
        double totalRevenue = getTotalRevenueBetween(start, end);
        int totalBills = getBillCountBetween(start, end);
        double totalExpense = expenseService.getTotalExpense();
        double profit = totalRevenue - totalExpense;
        
        System.out.println("┌─── TÀI CHÍNH ─────────────────────────────────────────────┐");
        System.out.printf("│ Tổng doanh thu (Bill PAID):  %,15.0f VNĐ       │%n", totalRevenue);
        System.out.printf("│ Số hóa đơn đã thanh toán:     %5d đơn                 │%n", totalBills);
        System.out.printf("│ Tổng chi phí:                 %,15.0f VNĐ       │%n", totalExpense);
        System.out.printf("│ Lợi nhuận ước tính:           %,15.0f VNĐ       │%n", profit);
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
        
        // Hoạt động
        System.out.println("┌─── HOẠT ĐỘNG HÔM NAY ─────────────────────────────────────┐");
        System.out.printf("│ Bàn trống:       %5d bàn                                │%n", tableService.getAvailableTables().size());
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    // Helper methods để tính doanh thu từ Bill
    private boolean inRange(LocalDateTime dt, LocalDate start, LocalDate end) {
        if (dt == null) return start == null && end == null;
        if (start == null && end == null) return true;
        LocalDate d = dt.toLocalDate();
        if (start != null && d.isBefore(start)) return false;
        if (end != null && d.isAfter(end)) return false;
        return true;
    }
    
    private double getTotalRevenueBetween(LocalDate start, LocalDate end) {
        return billService.getAll().stream()
            .filter(b -> b.getStatus() == BillStatus.PAID)
            .filter(b -> inRange(b.getClosedAt(), start, end))
            .mapToDouble(b -> b.getTotalAmount().getAmount().doubleValue())
            .sum();
    }
    
    private int getBillCountBetween(LocalDate start, LocalDate end) {
        return (int) billService.getAll().stream()
            .filter(b -> b.getStatus() == BillStatus.PAID)
            .filter(b -> inRange(b.getClosedAt(), start, end))
            .count();
    }
    
    /**
     * Báo cáo doanh thu chi tiết
     */
    public void printRevenueReport() {
        printRevenueReport(null, null);
    }
    
    public void printRevenueReport(LocalDate start, LocalDate end) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          BÁO CÁO DOANH THU CHI TIẾT                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        if (start != null && end != null) {
            System.out.println("Khoảng: " + start + " -> " + end);
        }
        
        // Doanh thu từ Bill PAID
        double totalRevenue = getTotalRevenueBetween(start, end);
        int totalBills = getBillCountBetween(start, end);
        double avgBillValue = totalBills > 0 ? totalRevenue / totalBills : 0;
        
        System.out.println("\n1. DOANH THU TỪ HÓA ĐƠN (PAID):");
        System.out.printf("   - Tổng: %,15.0f VNĐ (%d hóa đơn)%n", totalRevenue, totalBills);
        System.out.printf("   - Trung bình/hóa đơn: %,.0f VNĐ%n", avgBillValue);
        
        System.out.println();
    }
    
    /**
     * Báo cáo chi phí
     */
    public void printExpenseReport() {
        printExpenseReport(null, null);
    }
    
    public void printExpenseReport(LocalDate start, LocalDate end) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          BÁO CÁO CHI PHÍ                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        if (start != null && end != null) {
            System.out.println("Khoảng: " + start + " -> " + end);
        }
        
        double totalExpense = expenseService.getTotalExpense();
        double totalSalary = staffService.getTotalSalary();
        double otherExpense = totalExpense - totalSalary;
        
        System.out.printf("\nTổng chi phí: %,.0f VNĐ%n", totalExpense);
        System.out.printf("   - Lương nhân viên: %,.0f VNĐ (%.1f%%)%n", 
            totalSalary, totalExpense > 0 ? (totalSalary/totalExpense*100) : 0);
        System.out.printf("   - Chi phí khác: %,.0f VNĐ (%.1f%%)%n", 
            otherExpense, totalExpense > 0 ? (otherExpense/totalExpense*100) : 0);
        System.out.println();
        
        expenseService.printReport();
    }
    
    /**
     * Báo cáo kho
     */
    public void printInventoryReport() {
        printInventoryReport(null, null);
    }
    
    public void printInventoryReport(LocalDate start, LocalDate end) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          BÁO CÁO TỒN KHO                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        if (start != null && end != null) {
            System.out.println("Khoảng: " + start + " -> " + end);
        }
        
        inventoryService.printReport();
    }
    
    /**
     * Báo cáo đầy đủ (tất cả)
     */
    public void printFullReport() {
        printFullReport(null, null);
    }
    
    public void printFullReport(LocalDate start, LocalDate end) {
        printSystemOverview(start, end);
        printRevenueReport(start, end);
        printExpenseReport(start, end);
        printInventoryReport(start, end);
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("              KẾT THÚC BÁO CÁO");
        System.out.println("═══════════════════════════════════════════════════════════\n");
    }
    
    /**
     * Xuất báo cáo hàng ngày
     */
    public void printDailyReport() {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          BÁO CÁO HOẠT ĐỘNG HÔM NAY                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("Ngày: " + LocalDate.now());
        System.out.println();
        
        System.out.printf("Thanh toán:      %,15.0f VNĐ%n", paymentService.getTodayAmount());
        System.out.println();
        
        // Top món bán chạy có thể thêm nếu có logic tracking
        System.out.println("Trạng thái bàn:");
        tableService.printReport();
        
        System.out.println();
    }
}

