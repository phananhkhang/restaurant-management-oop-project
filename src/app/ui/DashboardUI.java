package app.ui;

import java.util.Scanner;

import service.StaffService;
import service.TableEntityService;
import service.BillService;
import service.InventoryItemService;

public class DashboardUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);

    // ===== PHƯƠNG THỨC TIỆN ÍCH =====

    private static void pause() {
        System.out.print("\n⏸️  Nhấn Enter để tiếp tục...");
        scanner.nextLine();
    }

    private static String repeat(String str, int count) {
        return str.repeat(count);
    }

    // ===== MENU DASHBOARD =====

    public static void dashboardMenu() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           🎯 DASHBOARD - TỔNG QUAN HỆ THỐNG          ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        // Thống kê nhân viên
        System.out.println("\n👨‍💼 NHÂN VIÊN:");
        System.out.println("   - Tổng số: " + getStaffCount());
        
        // Thống kê bàn
        System.out.println("\n🪑 BÀN:");
        System.out.println("   - Tổng số: " + getTableCount());
        System.out.println("   - Bàn trống: " + getAvailableTableCount());
        System.out.println("   - Bàn đang phục vụ: " + (getTableCount() - getAvailableTableCount()));
        
        // Thống kê hóa đơn
        System.out.println("\n🧾 HÓA ĐƠN:");
        System.out.println("   - Tổng số: " + getBillCount());
        
        // Thống kê thanh toán hôm nay
        System.out.println("\n💰 THANH TOÁN HÔM NAY:");
        System.out.printf("   - Doanh thu: %,.0f VNĐ%n", getTodayRevenue());
        
        // Thống kê kho
        System.out.println("\n📦 KHO:");
        System.out.println("   - Tổng mặt hàng: " + getInventoryCount());
        long lowStock = getLowStockCount();
        if (lowStock > 0) {
            System.out.println("   ⚠️  Cảnh báo: " + lowStock + " mặt hàng sắp hết!");
        }
        
        System.out.println("\n" + repeat("═", 55));
        pause();
    }

    // ===== STATIC SERVICES (sẽ được inject từ MainApp) =====
    
    private static StaffService staffService;
    private static TableEntityService tableService;
    private static BillService billService;
    private static InventoryItemService inventoryService;

    // ===== SETTERS =====
    
    public static void setStaffService(StaffService service) {
        staffService = service;
    }
    
    public static void setTableService(TableEntityService service) {
        tableService = service;
    }
    
    public static void setBillService(BillService service) {
        billService = service;
    }
    
    public static void setInventoryService(InventoryItemService service) {
        inventoryService = service;
    }

    // ===== HELPER METHODS =====
    
    private static int getStaffCount() {
        return staffService != null ? staffService.count() : 0;
    }
    
    private static int getTableCount() {
        return tableService != null ? tableService.count() : 0;
    }
    
    private static long getAvailableTableCount() {
        if (tableService == null) return 0;
        return tableService.getAll().stream()
            .filter(t -> t.getStatus().name().equals("AVAILABLE"))
            .count();
    }
    private static int getBillCount() {
        return billService != null ? billService.count() : 0;
    }
    
    private static double getTodayRevenue() {
        if (billService == null) return 0;
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // Tính doanh thu từ Bill đã thanh toán (PAID) hôm nay
        return billService.getAll().stream()
            .filter(b -> b.getStatus() == model.enums.BillStatus.PAID)
            .filter(b -> {
                if (b.getClosedAt() == null) return false;
                return b.getClosedAt().toLocalDate().equals(today);
            })
            .mapToDouble(b -> b.getTotalAmount().getAmount().doubleValue())
            .sum();
    }
    
    private static int getInventoryCount() {
        return inventoryService != null ? inventoryService.count() : 0;
    }
    
    private static long getLowStockCount() {
        if (inventoryService == null) return 0;
        return inventoryService.getAll().stream()
            .filter(item -> item.getQuantityOnHand() < 10)
            .count();
    }
}
