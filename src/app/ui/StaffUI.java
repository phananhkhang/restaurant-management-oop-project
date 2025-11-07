package app.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import model.Staff;
import service.StaffService;
import model.enums.Role;
import model.enums.StaffStatus;
import util.IdGenerator;
import util.Authentication;
import app.MainApp;

public class StaffUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);
    
    private static StaffService getStaffService() {
        return MainApp.getStaffService();
    }

    // ===== PHƯƠNG THỨC TIỆN ÍCH =====

    private static String getStringInput() {
        try {
            return scanner.nextLine().trim();
        } catch (Exception e) {
            scanner.nextLine();
            return "";
        }
    }

    private static int getIntInput() {
        try {
            int value = scanner.nextInt();
            scanner.nextLine();
            return value;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    private static double getDoubleInput() {
        try {
            double value = scanner.nextDouble();
            scanner.nextLine();
            return value;
        } catch (Exception e) {
            scanner.nextLine();
            return 0.0;
        }
    }

    private static void pause() {
        System.out.print("\n⏸️  Nhấn Enter để tiếp tục...");
        scanner.nextLine();
    }

    private static String repeat(String str, int count) {
        return str.repeat(count);
    }

    // ===== MENU NHÂN VIÊN =====

    public static void staffMenu() {
        // Kiểm tra quyền quản lý nhân viên
        if (!Authentication.canManageStaff()) {
            System.out.println("❌ Bạn không có quyền truy cập chức năng này!");
            System.out.println("   Chỉ MANAGER mới có thể quản lý nhân viên.");
            pause();
            return;
        }
        
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           👨‍💼 QUẢN LÝ NHÂN VIÊN                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. Xem danh sách & Quản lý nhân viên");
            System.out.println(" 2. Thống kê nhân viên");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: viewStaffListWithSearch(); break;
                case 2: getStaffService().printReport(); pause(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private static void viewStaffListWithSearch() {
        while (true) {
            listAllStaff();
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println(" 1. 🔍 Tìm kiếm nhân viên");
            System.out.println(" 2. ➕ Thêm nhân viên mới");
            System.out.println(" 3. ✏️  Cập nhật thông tin nhân viên");
            System.out.println(" 4. 🗑️  Xóa nhân viên");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: searchStaffMenu(); break;
                case 2: addNewStaff(); break;
                case 3: updateStaff(); break;
                case 4: deleteStaff(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!"); pause();
            }
        }
    }
    
    private static void searchStaffMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           🔍 TÌM KIẾM NHÂN VIÊN                       ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. Tìm theo ID");
            System.out.println(" 2. Tìm theo tên");
            System.out.println(" 3. Tìm theo số điện thoại");
            System.out.println(" 4. Tìm theo địa chỉ");
            System.out.println(" 5. Tìm theo vai trò");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: searchStaffById(); break;
                case 2: searchStaffByName(); break;
                case 3: searchStaffByPhone(); break;
                case 4: searchStaffByAddress(); break;
                case 5: searchStaffByRole(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!"); pause();
            }
        }
    }
    
    private static void listAllStaff() {
        List<Staff> staffList = getStaffService().getAll();
        System.out.println("\n📋 DANH SÁCH NHÂN VIÊN (" + staffList.size() + ")");
        System.out.println(repeat("─", 100));
        System.out.printf("%-10s %-25s %-15s %-15s %-12s %-12s %-12s%n",
            "ID", "Họ tên", "SĐT", "Địa chỉ", "Vai trò", "Lương", "Trạng thái");
        System.out.println(repeat("─", 100));
        
        for (Staff s : staffList) {
            System.out.printf("%-10s %-25s %-15s %-15s %-12s %,12.0f %-12s%n",
                s.getId(), s.getFullName(), s.getPhone(),
                s.getAddress(), s.getRole(), s.getSalary(), s.getStatus());
        }
    }
    
    private static void searchStaffById() {
        System.out.print("\n🔍 Nhập ID nhân viên: ");
        String id = getStringInput();
        
        if (id == null || id.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập ID nhân viên!");
            pause();
            return;
        }
        
        Optional<Staff> staff = getStaffService().getById(id);
        if (staff.isPresent()) {
            Staff s = staff.get();
            displayStaffDetail(s);
        } else {
            System.out.println("❌ Không tìm thấy nhân viên với ID: " + id);
        }
        pause();
    }
    
    private static void searchStaffByName() {
        System.out.print("\n🔍 Nhập tên nhân viên: ");
        String name = getStringInput();
        
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập tên nhân viên!");
            pause();
            return;
        }
        
        List<Staff> staffList = getStaffService().searchByName(name);
        if (staffList.isEmpty()) {
            System.out.println("❌ Không tìm thấy nhân viên với tên: " + name);
        } else {
            System.out.println("\n✅ Tìm thấy " + staffList.size() + " nhân viên:");
            System.out.println(repeat("─", 100));
            System.out.printf("%-10s %-25s %-15s %-15s %-12s %-12s %-12s%n",
                "ID", "Họ tên", "SĐT", "Địa chỉ", "Vai trò", "Lương", "Trạng thái");
            System.out.println(repeat("─", 100));
            for (Staff s : staffList) {
                System.out.printf("%-10s %-25s %-15s %-15s %-12s %,12.0f %-12s%n",
                    s.getId(), s.getFullName(), s.getPhone(),
                    s.getAddress(), s.getRole(), s.getSalary(), s.getStatus());
            }
        }
        pause();
    }
    
    private static void searchStaffByPhone() {
        System.out.print("\n🔍 Nhập số điện thoại: ");
        String phone = getStringInput();
        
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập số điện thoại!");
            pause();
            return;
        }
        
        List<Staff> staffList = getStaffService().searchByPhone(phone);
        if (staffList.isEmpty()) {
            System.out.println("❌ Không tìm thấy nhân viên với SĐT: " + phone);
        } else {
            System.out.println("\n✅ Tìm thấy " + staffList.size() + " nhân viên:");
            System.out.println(repeat("─", 100));
            System.out.printf("%-10s %-25s %-15s %-15s %-12s %-12s %-12s%n",
                "ID", "Họ tên", "SĐT", "Địa chỉ", "Vai trò", "Lương", "Trạng thái");
            System.out.println(repeat("─", 100));
            for (Staff s : staffList) {
                System.out.printf("%-10s %-25s %-15s %-15s %-12s %,12.0f %-12s%n",
                    s.getId(), s.getFullName(), s.getPhone(),
                    s.getAddress(), s.getRole(), s.getSalary(), s.getStatus());
            }
        }
        pause();
    }
    
    private static void searchStaffByAddress() {
        System.out.print("\n🔍 Nhập địa chỉ: ");
        String address = getStringInput();
        
        if (address == null || address.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập địa chỉ!");
            pause();
            return;
        }
        
        List<Staff> staffList = getStaffService().searchByAddress(address);
        if (staffList.isEmpty()) {
            System.out.println("❌ Không tìm thấy nhân viên với địa chỉ: " + address);
        } else {
            System.out.println("\n✅ Tìm thấy " + staffList.size() + " nhân viên:");
            System.out.println(repeat("─", 100));
            System.out.printf("%-10s %-25s %-15s %-15s %-12s %-12s %-12s%n",
                "ID", "Họ tên", "SĐT", "Địa chỉ", "Vai trò", "Lương", "Trạng thái");
            System.out.println(repeat("─", 100));
            for (Staff s : staffList) {
                System.out.printf("%-10s %-25s %-15s %-15s %-12s %,12.0f %-12s%n",
                    s.getId(), s.getFullName(), s.getPhone(),
                    s.getAddress(), s.getRole(), s.getSalary(), s.getStatus());
            }
        }
        pause();
    }
    
    private static void searchStaffByRole() {
        System.out.println("\n🔍 CHỌN VAI TRÒ:");
        System.out.println(" 1. MANAGER (Quản lý)");
        System.out.println(" 2. CHEF (Đầu bếp)");
        System.out.println(" 3. WAITER (Phục vụ)");
        System.out.println(" 4. CASHIER (Thu ngân)");
        System.out.println(" 5. HOST (Tiếp tân)");
        System.out.println(" 0. Hủy");
        System.out.print("👉 Chọn vai trò: ");
        
        int choice = getIntInput();
        
        Role role = null;
        switch (choice) {
            case 1: role = Role.MANAGER; break;
            case 2: role = Role.CHEF; break;
            case 3: role = Role.WAITER; break;
            case 4: role = Role.CASHIER; break;
            case 5: role = Role.HOST; break;
            case 0: return;
            default:
                System.out.println("❌ Lựa chọn không hợp lệ!");
                pause();
                return;
        }
        
        List<Staff> staffList = getStaffService().findByRole(role);
        if (staffList.isEmpty()) {
            System.out.println("❌ Không tìm thấy nhân viên với vai trò: " + role);
        } else {
            System.out.println("\n✅ Tìm thấy " + staffList.size() + " nhân viên với vai trò " + role + ":");
            System.out.println(repeat("─", 100));
            System.out.printf("%-10s %-25s %-15s %-15s %-12s %-12s %-12s%n",
                "ID", "Họ tên", "SĐT", "Địa chỉ", "Vai trò", "Lương", "Trạng thái");
            System.out.println(repeat("─", 100));
            for (Staff s : staffList) {
                System.out.printf("%-10s %-25s %-15s %-15s %-12s %,12.0f %-12s%n",
                    s.getId(), s.getFullName(), s.getPhone(),
                    s.getAddress(), s.getRole(), s.getSalary(), s.getStatus());
            }
        }
        pause();
    }
    
    private static void displayStaffDetail(Staff s) {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           📋 THÔNG TIN CHI TIẾT NHÂN VIÊN            ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("ID:         " + s.getId());
        System.out.println("Họ tên:     " + s.getFullName());
        System.out.println("SĐT:        " + s.getPhone());
        System.out.println("Địa chỉ:    " + s.getAddress());
        System.out.println("Vai trò:    " + s.getRole());
        System.out.printf("Lương:      %,.0f VNĐ%n", s.getSalary());
        System.out.println("Username:   " + s.getUsername());
        System.out.println("Trạng thái: " + s.getStatus());
        System.out.println(repeat("═", 55));
    }
    
    private static void addNewStaff() {
        System.out.println("\n➕ THÊM NHÂN VIÊN MỚI");
        
        String id = IdGenerator.generateStaffId(getStaffService().getAll());
        System.out.println("🆔 ID tự động: " + id);
        
        System.out.print("Họ tên (hoặc gõ 0 để hủy): ");
        String name = getStringInput();
        if ("0".equals(name)) {
            System.out.println("↩️ Đã hủy thêm nhân viên.");
            pause();
            return;
        }
        if (name.trim().isEmpty()) {
            System.out.println("❌ Họ tên không được để trống!");
            pause();
            return;
        }
        
        System.out.print("SĐT: ");
        String phone = getStringInput();
        System.out.print("Địa chỉ: ");
        String address = getStringInput();
        System.out.print("Username: ");
        String username = getStringInput();
        if (username.trim().isEmpty()) {
            System.out.println("❌ Username không được để trống!");
            pause();
            return;
        }
        System.out.print("Password: ");
        String password = getStringInput();
        if (password.trim().isEmpty()) {
            System.out.println("❌ Password không được để trống!");
            pause();
            return;
        }
        
        // Nhập vai trò với validation
        Role role = null;
        while (role == null) {
            System.out.print("Vai trò (MANAGER/CHEF/WAITER/CASHIER/HOST, hoặc 0 để hủy): ");
            String roleInput = getStringInput().trim().toUpperCase();
            
            if ("0".equals(roleInput)) {
                System.out.println("↩️ Đã hủy thêm nhân viên.");
                pause();
                return;
            }
            
            if (roleInput.isEmpty()) {
                System.out.println("❌ Vai trò không được để trống! Vui lòng chọn: MANAGER, CHEF, WAITER, CASHIER, hoặc HOST");
                continue;
            }
            
            try {
                role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Vai trò không hợp lệ! Vui lòng chọn một trong: MANAGER, CHEF, WAITER, CASHIER, HOST");
                System.out.println("   Hoặc gõ 0 để hủy.");
            }
        }
        
        System.out.print("Lương: ");
        double salary = getDoubleInput();
        if (salary < 0) {
            System.out.println("❌ Lương không hợp lệ!");
            pause();
            return;
        }
        
        Staff staff = new Staff(id, name, phone, address, username, password,
            role, salary, StaffStatus.ACTIVE);
        
        try {
            getStaffService().create(staff);
            
            // 💾 Tự động lưu sau khi thêm nhân viên
            MainApp.autoSave();
            
            System.out.println("✅ Thêm nhân viên thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void updateStaff() {
        System.out.print("\n✏️  Nhập ID nhân viên cần sửa (hoặc 0 để hủy): ");
        String id = getStringInput();
        if ("0".equals(id)) {
            System.out.println("↩️ Đã hủy.");
            pause();
            return;
        }
        
        Optional<Staff> opt = getStaffService().getById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy!");
            pause();
            return;
        }
        
        Staff s = opt.get();
        System.out.println("\nThông tin hiện tại: " + s.getFullName() + " - " + s.getRole());
        System.out.println("\nNhập lương mới: ");
        double newSalary = getDoubleInput();
        if (newSalary >= 0) {
            s.setSalary(newSalary);
        }
        
        // Nhập trạng thái với validation
        System.out.println("\nNhập trạng thái mới (ACTIVE/INACTIVE, hoặc Enter để giữ nguyên): ");
        String statusInput = getStringInput().trim().toUpperCase();
        if (!statusInput.isEmpty()) {
            try {
                StaffStatus newStatus = StaffStatus.valueOf(statusInput);
                s.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Trạng thái không hợp lệ, giữ nguyên trạng thái cũ.");
            }
        }
        
        getStaffService().update(s);
        
        // 💾 Tự động lưu sau khi cập nhật nhân viên
        MainApp.autoSave();
        
        System.out.println("✅ Cập nhật thành công!");
        pause();
    }
    
    private static void deleteStaff() {
        System.out.print("\n🗑️  Nhập ID nhân viên cần xóa: ");
        String id = getStringInput();
        
        System.out.print("⚠️  Xác nhận xóa? (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            if (getStaffService().delete(id)) {
                // 💾 Tự động lưu sau khi xóa nhân viên
                MainApp.autoSave();
                
                System.out.println("✅ Xóa thành công!");
            } else {
                System.out.println("❌ Không tìm thấy!");
            }
        }
        pause();
    }
}
