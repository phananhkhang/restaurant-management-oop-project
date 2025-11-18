package app;

import service.*;
import util.Authentication;
import app.ui.*;
import java.util.Scanner; 

public class MainApp {
    // ===== KHỞI TẠO CÁC SERVICE =====
    private static StaffService staffService;
    private static MenuItemService menuService;
    private static TableEntityService tableService;
    private static InventoryItemService inventoryService;
    private static PaymentService paymentService;
    private static ExpenseService expenseService;
    private static ReportService reportService;
    private static BillService billService;
    private static ShiftService shiftService;
    private static StaffShiftService staffShiftService;
    private static IngredientService ingredientService; 
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_DIR = "data/";
    
    // username: m01
    // password: 123

    // ===== MAIN =====
    public static void main(String[] args) {
        // Thiết lập UTF-8 encoding cho console để hiển thị đúng Tiếng Việt và Unicode
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        
        try {
            printWelcome();
            initializeServices();
            loadAllData();
            
            // Yêu cầu đăng nhập trước khi vào hệ thống
            if (!login()) {
                System.out.println("❌ Đăng nhập thất bại. Thoát chương trình.");
                return;
            }
            
            showMainMenu();
        } catch (Exception e) {
            System.err.println("❌ Lỗi nghiêm trọng: " + e.getMessage());
            System.err.println("Chi tiết lỗi: " + e.getClass().getName());
        } finally {
            scanner.close();
        }
    }
    
    private static void printWelcome() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║                HỆ THỐNG QUẢN LÝ QUÁN NHẬU                  ║");
        System.out.println("║               Restaurant Management System                 ║");
        System.out.println("║                         Nhóm 7                             ║");
        System.out.println("║                 Thành viên thực hiện:                      ║");
        System.out.println("║                   - Phan Anh Khang                         ║");
        System.out.println("║                   - Đỗ Mạnh Huy                            ║");
        System.out.println("║                Báo cáo lúc: 7h30 19/11/2025                ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    // ===== KHỞI TẠO & LOAD DATA =====
    
    private static void initializeServices() {
        System.out.println("⏳ Đang khởi tạo hệ thống...");
        
        staffService = new StaffService(DATA_DIR + "staff.csv");
        menuService = new MenuItemService(DATA_DIR + "menu_items.csv");
        ingredientService = new IngredientService(); 
        tableService = new TableEntityService(DATA_DIR + "tables.csv");
        inventoryService = new InventoryItemService(DATA_DIR + "inventory.csv");
        paymentService = new PaymentService(DATA_DIR + "payments.csv");
        expenseService = new ExpenseService(DATA_DIR + "expenses.csv");
        billService = new BillService(DATA_DIR + "bills.csv");
        shiftService = new ShiftService(DATA_DIR + "shifts.csv");
        staffShiftService = new StaffShiftService(DATA_DIR + "staff_shifts.csv");
        
        reportService = new ReportService(
            staffService, menuService,
            inventoryService, paymentService, expenseService, tableService,
            billService 
        );
        
        // Khởi tạo Authentication service
        Authentication.setStaffService(staffService);
        
        // Khởi tạo các UI services
        initializeUIServices();
        
        System.out.println("✅ Khởi tạo thành công!");
    }
    
    private static void initializeUIServices() {
        // Khởi tạo DashboardUI
        DashboardUI.setStaffService(staffService);
        DashboardUI.setTableService(tableService);
        DashboardUI.setBillService(billService);
        DashboardUI.setInventoryService(inventoryService);
        
        // Khởi tạo ReportUI
        ReportUI.initializeReportService(
            staffService, menuService,
            inventoryService, paymentService, expenseService, tableService,
            billService  // Thêm billService
        );
    }
    
    public static StaffService getStaffService() { return staffService; }
    public static MenuItemService getMenuService() { return menuService; }
    public static TableEntityService getTableService() { return tableService; }
    public static InventoryItemService getInventoryService() { return inventoryService; }
    public static PaymentService getPaymentService() { return paymentService; }
    public static ExpenseService getExpenseService() { return expenseService; }
    public static ReportService getReportService() { return reportService; }
    public static BillService getBillService() { return billService; }
    public static ShiftService getShiftService() { return shiftService; }
    public static StaffShiftService getStaffShiftService() { return staffShiftService; }
    public static IngredientService getIngredientService() { return ingredientService; }

    private static void loadAllData() {
        System.out.println("\n⏳ Đang tải dữ liệu từ file CSV...");
        try {
            staffService.loadData();
            menuService.loadData();
            tableService.loadData();
            inventoryService.loadData();
            paymentService.loadData();
            expenseService.loadData();
            billService.loadData();
            shiftService.loadData();
            staffShiftService.loadData();
            
            System.out.println("✅ Tải dữ liệu thành công!");
            System.out.println("   - Nhân viên: " + staffService.count());
            System.out.println("   - Món ăn: " + menuService.count());
            System.out.println("   - Bàn: " + tableService.count());
            System.out.println("   - Tồn kho: " + inventoryService.count());
        } catch (Exception e) {
            System.err.println("⚠️  Lỗi khi tải dữ liệu: " + e.getMessage());
            System.err.println("   Chi tiết: " + e.getClass().getName());
            e.printStackTrace();
        }
    }
    
    private static void saveAllData() {
        System.out.println("\n⏳ Đang lưu dữ liệu...");
        try {
            staffService.saveData();
            menuService.saveData();
            tableService.saveData();
            inventoryService.saveData();
            paymentService.saveData();
            expenseService.saveData();
            
            // Lưu dữ liệu cho các service mới
            billService.saveData();
            shiftService.saveData();
            staffShiftService.saveData();
            System.out.println("✅ Lưu dữ liệu thành công!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tự động lưu dữ liệu sau mỗi thao tác quan trọng
     */
    public static void autoSave() {
        try {
            staffService.saveData();
            menuService.saveData();
            tableService.saveData();
            inventoryService.saveData();
            paymentService.saveData();
            expenseService.saveData();
            billService.saveData();
            shiftService.saveData();
            staffShiftService.saveData();
        } catch (Exception e) {
            System.err.println("⚠️  Lỗi tự động lưu: " + e.getMessage());
        }
    }
    
    // ===== AUTHENTICATION =====
    
    private static boolean login() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║                   ĐĂNG NHẬP HỆ THỐNG                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        int attempts = 0;
        int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("👤 Username: ");
            String username = getStringInput();
            System.out.print("🔒 Password: ");
            String password = getStringInput();
            
            if (Authentication.login(username, password)) {
                System.out.println("✅ Đăng nhập thành công!");
                showCurrentUserInfo();
                return true;
            } else {
                attempts++;
                System.out.println("❌ Username hoặc password không đúng!");
                if (attempts < maxAttempts) {
                    System.out.println("⚠️  Còn " + (maxAttempts - attempts) + " lần thử.");
                }
            }
        }
        
        System.out.println("🚫 Đã hết số lần thử đăng nhập!");
        return false;
    }
    
    private static void showCurrentUserInfo() {
        if (Authentication.getCurrentUser() != null) {
            System.out.println("👋 Chào mừng: " + Authentication.getCurrentUser().getFullName());
            System.out.println("🎭 Vai trò: " + Authentication.getCurrentUser().getRole());
        }
    }
    
    private static void logout() {
        Authentication.logout();
        System.out.println("👋 Đã đăng xuất thành công!");
    }
    
    // ===== MENU CHÍNH =====
    
   private static void showMainMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║               MENU CHÍNH                              ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1.  🎯 Dashboard / Tổng quan");
            System.out.println(" 2.  👨‍💼 Quản lý Nhân viên");
            System.out.println(" 3.  ⏰ Quản lý Ca làm việc");
            System.out.println(" 4.  🍽️  Quản lý Thực đơn");
            System.out.println(" 5.  🪑 Quản lý Bàn");
            System.out.println(" 6.  🧾 Quản lý hóa đơn");
            System.out.println(" 7.  📦 Quản lý Kho & Tồn kho");
            System.out.println(" 8.  📊 Báo cáo & Thống kê ");
            System.out.println(" 9.  🔄 Đăng xuất");
            System.out.println(" 0.  🚪 Thoát");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn chức năng: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                 case 1:DashboardUI.dashboardMenu();break;
                case 2:StaffUI.staffMenu();break;
                case 3:ShiftUI.shiftManagementMenu();break;
                case 4:MenuItemUI.menuItemMenu();break;
                case 5:TableUI.tableMenu();break;
                case 6:BillUI.billOrderMenu();break;
                case 7:InventoryUI.inventoryMenu(); break;
                case 8:ReportUI.reportMenu();break;
                case 9:logout();if (!login()) {System.out.println("❌ Đăng nhập thất bại. Thoát chương trình.");return;}
                    else {
                        System.out.println("✅ Đăng nhập thành công!");
                        showCurrentUserInfo();
                    }
                    break;
                case 0:
                    System.out.print("\n💾 Lưu dữ liệu trước khi thoát? (y/n): ");
                    if (getStringInput().equalsIgnoreCase("y")) {
                        saveAllData();
                    }
                    System.out.println("\n👋 Tạm biệt! Hẹn gặp lại!");
                    return;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }
    private static String getStringInput() {
        return scanner.nextLine().trim();
    }
}