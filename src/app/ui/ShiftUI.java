package app.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import model.Shift;
import model.StaffShift;
import service.ShiftService;
import service.StaffShiftService;
import model.enums.ShiftType;
import model.enums.StaffShiftStatus;
import util.IdGenerator;
import app.MainApp;

public class ShiftUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);
    
    private static ShiftService getShiftService() {
        return MainApp.getShiftService();
    }
    
    private static StaffShiftService getStaffShiftService() {
        return MainApp.getStaffShiftService();
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

    private static void pause() {
        System.out.print("\n⏸️  Nhấn Enter để tiếp tục...");
        scanner.nextLine();
    }

    private static String repeat(String str, int count) {
        return str.repeat(count);
    }

    // ===== MENU CA LÀM VIỆC =====

    public static void shiftManagementMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           ⏰ QUẢN LÝ CA LÀM VIỆC                      ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. Xem danh sách ca làm việc & Quản lý");
            System.out.println(" 2. Xem phân công nhân viên & Quản lý");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: viewShiftsWithManagement(); break;
                case 2: viewStaffAssignmentsWithManagement(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private static void viewStaffAssignmentsWithManagement() {
        while (true) {
            listStaffShiftAssignments();
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println(" 1. ➕ Phân công nhân viên vào ca");
            System.out.println(" 2. ✏️  Sửa phân công ca làm việc");
            System.out.println(" 3. 🗑️  Xóa nhân viên khỏi ca");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: listAllShifts(); assignStaffToShift(); break;
                case 2: updateStaffShiftAssignment(); break;
                case 3: deleteStaffShiftAssignment(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!"); pause();
            }
        }
    }
    
    private static void viewShiftsWithManagement() {
        while (true) {
            listAllShifts();
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println(" 1. ➕ Thêm ca làm việc mới");
            System.out.println(" 2. ✏️  Sửa ca làm việc");
            System.out.println(" 3. 🗑️  Xóa ca làm việc");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: addNewShift(); break;
                case 2: updateShift(); break;
                case 3: deleteShift(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!"); pause();
            }
        }
    }
    
    private static void listAllShifts() {
        List<Shift> shifts = getShiftService().getAll();
        System.out.println("\n📋 DANH SÁCH CA LÀM VIỆC (" + shifts.size() + ")");
        System.out.println(repeat("─", 80));
        System.out.printf("%-10s %-25s %-12s %-12s %-15s%n",
            "Mã ca", "Tên ca", "Bắt đầu", "Kết thúc", "Loại ca");
        System.out.println(repeat("─", 80));
        
        for (Shift s : shifts) {
            System.out.printf("%-10s %-25s %-12s %-12s %-15s%n",
                s.getShiftId(), s.getShiftName(), s.getStartTime(), 
                s.getEndTime(), s.getShiftType());
        }
    }
    
    private static void addNewShift() {
        System.out.println("\n➕ THÊM CA LÀM VIỆC MỚI");
        
        List<Shift> existingShifts = getShiftService().getAll();
        String id = IdGenerator.generateId("SH", existingShifts, Shift::getShiftId);
        System.out.println("🆔 ID tự động: " + id);
        
        System.out.print("Tên ca: ");
        String name = getStringInput();
        System.out.print("Giờ bắt đầu (HH:mm): ");
        String startTime = getStringInput();
        System.out.print("Giờ kết thúc (HH:mm): ");
        String endTime = getStringInput();
        
        System.out.println("\n📋 Loại ca:");
        System.out.println("  1. MORNING   (Ca sáng)");
        System.out.println("  2. AFTERNOON (Ca chiều)");
        System.out.println("  3. EVENING   (Ca tối)");
        System.out.println("  4. NIGHT     (Ca đêm)");
        System.out.print("Chọn loại ca (1-4): ");
        int typeChoice = getIntInput();
        
        ShiftType shiftType = ShiftType.MORNING;
        switch (typeChoice) {
            case 1: shiftType = ShiftType.MORNING; break;
            case 2: shiftType = ShiftType.AFTERNOON; break;
            case 3: shiftType = ShiftType.EVENING; break;
            case 4: shiftType = ShiftType.NIGHT; break;
            default: 
                System.out.println("⚠️  Lựa chọn không hợp lệ, mặc định chọn MORNING");
        }
        
        Shift shift = new Shift(id, name, startTime, endTime, shiftType);
        try {
            getShiftService().create(shift);
            
            // 💾 Tự động lưu sau khi thêm ca làm việc
            MainApp.autoSave();
            
            System.out.println("✅ Thêm ca làm việc thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void updateShift() {
        System.out.print("\n✏️  Nhập mã ca cần sửa: ");
        String id = getStringInput();
        
        if (id.isEmpty()) {
            System.out.println("❌ Mã ca không được để trống!");
            pause();
            return;
        }
        
        // Kiểm tra ca có tồn tại không
        var shiftOpt = getShiftService().getById(id);
        if (shiftOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy ca làm việc với mã: " + id);
            pause();
            return;
        }
        
        Shift existingShift = shiftOpt.get();
        
        // Hiển thị thông tin hiện tại
        System.out.println("\n📋 THÔNG TIN HIỆN TẠI:");
        System.out.println("  Mã ca: " + existingShift.getShiftId());
        System.out.println("  Tên ca: " + existingShift.getShiftName());
        System.out.println("  Giờ bắt đầu: " + existingShift.getStartTime());
        System.out.println("  Giờ kết thúc: " + existingShift.getEndTime());
        System.out.println("  Loại ca: " + existingShift.getShiftType());
        
        System.out.println("\n✏️  NHẬP THÔNG TIN MỚI (Enter để giữ nguyên):");
        
        System.out.print("Tên ca mới [" + existingShift.getShiftName() + "]: ");
        String newName = getStringInput();
        if (newName.isEmpty()) newName = existingShift.getShiftName();
        
        System.out.print("Giờ bắt đầu mới (HH:mm) [" + existingShift.getStartTime() + "]: ");
        String newStartTime = getStringInput();
        if (newStartTime.isEmpty()) newStartTime = existingShift.getStartTime();
        
        System.out.print("Giờ kết thúc mới (HH:mm) [" + existingShift.getEndTime() + "]: ");
        String newEndTime = getStringInput();
        if (newEndTime.isEmpty()) newEndTime = existingShift.getEndTime();
        
        System.out.println("\n📋 Loại ca:");
        System.out.println("  1. MORNING   (Ca sáng)");
        System.out.println("  2. AFTERNOON (Ca chiều)");
        System.out.println("  3. EVENING   (Ca tối)");
        System.out.println("  4. NIGHT     (Ca đêm)");
        System.out.print("Chọn loại ca (1-4) [" + existingShift.getShiftType() + "]: ");
        String typeInput = getStringInput();
        
        ShiftType newShiftType = existingShift.getShiftType();
        if (!typeInput.isEmpty()) {
            try {
                int typeChoice = Integer.parseInt(typeInput);
                switch (typeChoice) {
                    case 1: newShiftType = ShiftType.MORNING; break;
                    case 2: newShiftType = ShiftType.AFTERNOON; break;
                    case 3: newShiftType = ShiftType.EVENING; break;
                    case 4: newShiftType = ShiftType.NIGHT; break;
                    default: 
                        System.out.println("⚠️  Lựa chọn không hợp lệ, giữ nguyên loại ca cũ");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Lựa chọn không hợp lệ, giữ nguyên loại ca cũ");
            }
        }
        
        // Cập nhật ca làm việc
        Shift updatedShift = new Shift(id, newName, newStartTime, newEndTime, newShiftType);
        
        try {
            getShiftService().update(updatedShift);
            
            // 💾 Tự động lưu sau khi cập nhật ca làm việc
            MainApp.autoSave();
            
            System.out.println("✅ Cập nhật ca làm việc thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void listStaffShiftAssignments() {
        List<StaffShift> assignments = getStaffShiftService().getAll();
        System.out.println("\n📋 PHÂN CÔNG NHÂN VIÊN (" + assignments.size() + ")");
        System.out.println(repeat("─", 90));
        System.out.printf("%-15s %-15s %-15s %-20s %-20s%n",
            "ID Phân công", "Mã NV", "Mã ca", "Ngày làm", "Trạng thái");
        System.out.println(repeat("─", 90));
        
        for (StaffShift ss : assignments) {
            System.out.printf("%-15s %-15s %-15s %-20s %-20s%n",
                ss.getStaffShiftId(), 
                ss.getStaffId(), 
                ss.getShiftId(),
                ss.getWorkDate(), 
                ss.getStatus());
        }
    }
    
    private static void assignStaffToShift() {
        System.out.println("\n➕ PHÂN CÔNG NHÂN VIÊN VÀO CA");
        
        System.out.print("Mã nhân viên: ");
        String staffId = getStringInput();
        
        System.out.print("Mã ca: ");
        String shiftId = getStringInput();
        
        System.out.print("Ngày làm việc (yyyy-MM-dd): ");
        String workDate = getStringInput();
        
        List<StaffShift> existingAssignments = getStaffShiftService().getAll();
        String id = IdGenerator.generateId("SS", existingAssignments, StaffShift::getStaffShiftId);
        StaffShift ss = new StaffShift(id, staffId, shiftId, StaffShiftStatus.SCHEDULED, workDate);
        
        try {
            getStaffShiftService().create(ss);
            
            // 💾 Tự động lưu sau khi phân công nhân viên
            MainApp.autoSave();
            
            System.out.println("✅ Phân công thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void updateStaffShiftAssignment() {
        System.out.println("\n✏️  SỬA PHÂN CÔNG CA LÀM VIỆC");
        
        System.out.print("Mã phân công (SS***): ");
        String id = getStringInput();
        
        if (id == null || id.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập mã phân công!");
            pause();
            return;
        }
        
        Optional<StaffShift> existingOpt = getStaffShiftService().getById(id);
        if (!existingOpt.isPresent()) {
            System.out.println("❌ Không tìm thấy phân công với mã: " + id);
            pause();
            return;
        }
        
        StaffShift existing = existingOpt.get();
        
        System.out.println("\n📋 Thông tin hiện tại:");
        System.out.println("  Mã NV: " + existing.getStaffId());
        System.out.println("  Mã ca: " + existing.getShiftId());
        System.out.println("  Ngày làm: " + existing.getWorkDate());
        System.out.println("  Trạng thái: " + existing.getStatus());
        
        System.out.print("\nMã nhân viên mới (Enter = giữ nguyên): ");
        String staffId = getStringInput();
        if (staffId == null || staffId.trim().isEmpty()) {
            staffId = existing.getStaffId();
        }
        
        System.out.print("Mã ca mới (Enter = giữ nguyên): ");
        String shiftId = getStringInput();
        if (shiftId == null || shiftId.trim().isEmpty()) {
            shiftId = existing.getShiftId();
        }
        
        System.out.print("Ngày làm việc mới (yyyy-MM-dd, Enter = giữ nguyên): ");
        String workDate = getStringInput();
        if (workDate == null || workDate.trim().isEmpty()) {
            workDate = existing.getWorkDate();
        }
        
        System.out.println("\n📋 Trạng thái:");
        System.out.println("  1. SCHEDULED (Đã lên lịch)");
        System.out.println("  2. COMPLETED (Hoàn thành)");
        System.out.println("  3. ABSENT (Vắng mặt)");
        System.out.print("Chọn trạng thái (Enter = giữ nguyên): ");
        String statusInput = getStringInput();
        
        StaffShiftStatus status = existing.getStatus();
        if (statusInput != null && !statusInput.trim().isEmpty()) {
            try {
                int choice = Integer.parseInt(statusInput);
                switch (choice) {
                    case 1: status = StaffShiftStatus.SCHEDULED; break;
                    case 2: status = StaffShiftStatus.COMPLETED; break;
                    case 3: status = StaffShiftStatus.ABSENT; break;
                    default:
                        System.out.println("⚠️  Lựa chọn không hợp lệ, giữ nguyên trạng thái cũ.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Giá trị không hợp lệ, giữ nguyên trạng thái cũ.");
            }
        }
        
        StaffShift updated = new StaffShift(id, staffId, shiftId, status, workDate);
        
        try {
            getStaffShiftService().update(updated);
            
            // 💾 Tự động lưu sau khi cập nhật
            MainApp.autoSave();
            
            System.out.println("✅ Cập nhật phân công thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void deleteStaffShiftAssignment() {
        System.out.println("\n🗑️  XÓA NHÂN VIÊN KHỎI CA LÀM VIỆC");
        
        System.out.print("Mã phân công (SS***): ");
        String id = getStringInput();
        
        if (id == null || id.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập mã phân công!");
            pause();
            return;
        }
        
        Optional<StaffShift> existingOpt = getStaffShiftService().getById(id);
        if (!existingOpt.isPresent()) {
            System.out.println("❌ Không tìm thấy phân công với mã: " + id);
            pause();
            return;
        }
        
        StaffShift existing = existingOpt.get();
        System.out.println("\n📋 Thông tin phân công:");
        System.out.println("  Mã NV: " + existing.getStaffId());
        System.out.println("  Mã ca: " + existing.getShiftId());
        System.out.println("  Ngày làm: " + existing.getWorkDate());
        System.out.println("  Trạng thái: " + existing.getStatus());
        
        System.out.print("\n❓ Xác nhận xóa? (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            if (getStaffShiftService().delete(id)) {
                // 💾 Tự động lưu sau khi xóa
                MainApp.autoSave();
                
                System.out.println("✅ Xóa phân công thành công!");
            } else {
                System.out.println("❌ Không thể xóa phân công!");
            }
        } else {
            System.out.println("↩️  Đã hủy thao tác xóa.");
        }
        pause();
    }
    
    private static void deleteShift() {
        System.out.print("\n🗑️  Nhập mã ca cần xóa: ");
        String id = getStringInput();
        
        System.out.print("Xác nhận xóa? (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            if (getShiftService().delete(id)) {
                // 💾 Tự động lưu sau khi xóa ca làm việc
                MainApp.autoSave();
                
                System.out.println("✅ Xóa ca làm việc thành công!");
            } else {
                System.out.println("❌ Không tìm thấy ca làm việc!");
            }
        }
        pause();
    }
}
