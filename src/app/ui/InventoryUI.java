package app.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import model.InventoryItem;
import service.InventoryItemService;
import model.enums.Unit;
import model.enums.InventoryStatus;
import util.IdGenerator;
import app.MainApp;
import util.Authentication;

public class InventoryUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);
    
    private static InventoryItemService getInventoryService() {
        return MainApp.getInventoryService();
    }

    // ===== PHƯƠNG THỨC TIỆN ÍCH =====

    private static String getStringInput() {
        try {
            return scanner.nextLine().trim();
        } catch (Exception e) {
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

    // ===== MENU KHO =====

    public static void inventoryMenu() {
        // Kiểm tra quyền quản lý kho
        if (!Authentication.canManageInventory()) {
            System.out.println("❌ Bạn không có quyền truy cập chức năng này!");
            System.out.println("   Chỉ MANAGER và CHEF mới có thể quản lý kho.");
            pause();
            return;
        }
        
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           📦 QUẢN LÝ KHO                              ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. Xem danh sách");
            System.out.println(" 2. Thống kê kho");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1: listAllInventoryItems(); break;
                 case 2: getInventoryService().printReport(); pause(); break;
                 case 0: return;
                 default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private static void listAllInventoryItems() {
        while (true) {
            // Hiển thị bảng ngay lập tức
            List<InventoryItem> items = getInventoryService().getAll();
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           📋 DANH SÁCH                 ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println("\n📦 TỒN KHO (" + items.size() + " loại)");
            System.out.println(repeat("─", 102));
            System.out.printf("%-10s %-30s %-13s %-15s %-15s%n",
                "Mã", "Tên", "Đơn vị", "Tồn kho", "Trạng thái");
            System.out.println(repeat("─", 102));
            
            for (InventoryItem item : items) {
                System.out.printf("%-10s %-30s %-13s %-15s %-15s%n",
                    item.getStockItemId(), item.getName(), item.getUnit(),
                    item.getQuantityOnHand(), item.getStatus());
            }
            
            // Menu hành động
            System.out.println("\n" + repeat("─", 102));
            System.out.println("🔧 HÀNH ĐỘNG:");
            System.out.println(" 1. ➕ Thêm vào kho");
            System.out.println(" 2. ✏️  Sửa hàng tồn kho");
            System.out.println(" 3. 🗑️  Xóa hàng tồn kho");
            System.out.println(" 4. 🔍 Tìm kiếm");
            System.out.println(" 5. 📥 Nhập kho (tăng tồn)");
            System.out.println(" 6. 📤 Xuất kho (giảm tồn)");
            System.out.println(" 0. Quay lại");
            System.out.println(repeat("─", 102));
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1: addNewInventoryItem(); break;
                case 2: updateInventoryItem(); break;
                case 3: deleteInventoryItem(); break;
                case 4: searchInventoryItem(); break;
                case 5: increaseStock(); break;
                case 6: decreaseStock(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    //  ==== Nhập kho =====
    private static void increaseStock() {
        while (true) {
            // Sửa định dạng hiển thị
            System.out.print("\n📥 NHẬP KHO - Mã hàng kho (gõ 0 để quay lại): "); 
            String id = getStringInput();
            if (id == null) id = "";

            // 0 = quay lại (im lặng)
            if ("0".equals(id)) return;

            // rỗng => hỏi tiếp hoặc quay lại
            if (id.isEmpty()) {
                System.out.println("❌ Mã không được để trống.");
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 0) return;
                else continue;
            }
            
            var opt = getInventoryService().getById(id);
            if (opt.isEmpty()) {
                System.out.println("❌ Không tìm thấy hàng tồn kho với mã: " + id);
                // sau lỗi cho phép nhập lại hoặc quay về
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 0) return;
                else continue;
            }

            InventoryItem item = opt.get();
            System.out.println("🔎 " + item.getName() + " — Hiện có: " + item.getQuantityOnHand() + " " + item.getUnit());
            System.out.print("Số lượng nhập (hoặc 0 để hủy): ");
            double quantity = getDoubleInput();
            if (quantity == 0) {
                // hủy thao tác, hỏi quay lại hay nhập tiếp
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 0) return;
                else continue;
            }
            if (quantity < 0) {
                System.out.println("↩️ Số lượng không hợp lệ (phải >= 0), hủy thao tác.");
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 0) return;
                else continue;
            }

            try {
                getInventoryService().increaseStock(id, quantity);
                InventoryItem updated = getInventoryService().getById(id).orElse(item);
                
                // 💾 Tự động lưu vào inventory.csv
                MainApp.autoSave();
                
                System.out.println("✅ Nhập kho thành công! Tồn hiện tại: " + updated.getQuantityOnHand() + " " + updated.getUnit());
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi nhập kho: " + e.getMessage());
            }

            // Sau mỗi thao tác hỏi tiếp tục hay quay lại menu trước
            System.out.println("\n🔁 1. Nhập tiếp mặt hàng");
            System.out.println("   0. Quay lại danh sách");
            System.out.print("👉 Chọn: ");
            int next = getIntInput();
            if (next == 1) {
                continue;
            } else {
                return;
            }
        }
    }
    //  ==== Xuất kho =====
   private static void decreaseStock() {
        while (true) {
            // Sửa định dạng hiển thị
            System.out.print("\n📤 XUẤT KHO - Mã hàng kho (gõ 0 để quay lại): ");
            String id = getStringInput();
            if (id == null) id = "";

            // 0 = quay lại (im lặng)
            if ("0".equals(id)) return;

            // rỗng => hỏi tiếp hoặc quay lại
            if (id.isEmpty()) {
                System.out.println("❌ Mã không được để trống.");
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 0) return;
                else continue;
            }

            var opt = getInventoryService().getById(id);
            if (opt.isEmpty()) {
                System.out.println("❌ Không tìm thấy hàng tồn kho với mã: " + id);
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 1) continue;
                return;
            }

            InventoryItem item = opt.get();
            System.out.println("🔎 " + item.getName() + " — Hiện có: " + item.getQuantityOnHand() + " " + item.getUnit());
            System.out.print("Số lượng xuất (hoặc 0 để hủy): ");
            double quantity = getDoubleInput();
            if (quantity == 0) {
                // hỏi tiếp hay quay lại
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 1) continue;
                return;
            }
            if (quantity < 0) {
                System.out.println("↩️ Số lượng không hợp lệ (phải >= 0), hủy thao tác.");
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 1) continue;
                return;
            }

            if (quantity > item.getQuantityOnHand()) {
                System.out.println("⚠️ Số lượng xuất vượt quá tồn hiện tại. Thao tác bị hủy.");
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int choice = getIntInput();
                if (choice == 1) continue;
                return;
            }

            try {
                getInventoryService().decreaseStock(id, quantity);
                InventoryItem updated = getInventoryService().getById(id).orElse(item);
                
                // 💾 Tự động lưu vào inventory.csv
                MainApp.autoSave();
                
                System.out.println("✅ Xuất kho thành công! Tồn hiện tại: " + updated.getQuantityOnHand() + " " + updated.getUnit());
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi xuất kho: " + e.getMessage());
            }

            System.out.println("\n🔁 1. Xuất tiếp mặt hàng");
            System.out.println("   0. Quay lại danh sách");
            System.out.print("👉 Chọn: ");
            int next = getIntInput();
            if (next == 1) continue;
            return;
        }
    }

    //  ==== Thêm =====
    private static void addNewInventoryItem() {
        while (true) {
            System.out.println("\n➕ THÊM hàng tồn kho MỚI");
            String id = IdGenerator.generateInventoryId(getInventoryService().getAll());
            System.out.println("🆔 ID tự động: " + id);

            System.out.print("Tên (gõ 0 để quay lại): ");
            String name = getStringInput();
            if (name == null) name = "";
            if (name.equals("0")) return;
            if (name.trim().isEmpty()) {
                System.out.println("❌ Tên không được để trống.");
                System.out.println("\n🔁 1. Nhập lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int c = getIntInput();
                if (c == 0) return;
                else continue;
            }

            // chọn đơn vị
            System.out.println("\n🧾 ĐƠN VỊ (chọn số):");
            System.out.println(" 1. 🟦  PCS   — chiếc");
            System.out.println(" 2. 📦  BOX   — hộp");
            System.out.println(" 3. 🧩  PACK  — gói");
            System.out.println(" 4. ⚖️  KG    — kilô");
            System.out.println(" 5. 🧾  G     — gam");
            System.out.println(" 6. 🧴  LITER — lít");
            System.out.println(" 7. 💧  ML    — mililit");
            System.out.print("👉 Chọn đơn vị (1-7, hoặc 0 để quay lại): ");
            int unitChoice = getIntInput();
            if (unitChoice == 0) return;
            if (unitChoice < 1 || unitChoice > Unit.values().length) {
                System.out.println("❌ Lựa chọn đơn vị không hợp lệ.");
                System.out.println("\n🔁 1. Thử lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int c = getIntInput();
                if (c == 1) continue;
                return;
            }
            Unit unit = Unit.values()[unitChoice - 1];

            System.out.print("Số lượng tồn ban đầu: ");
            double quantity = getDoubleInput();
            if (quantity < 0) {
                System.out.println("❌ Số lượng không hợp lệ (phải >= 0).");
                System.out.println("\n🔁 1. Thử lại");
                System.out.println("   0. Quay lại");
                System.out.print("👉 Chọn: ");
                int c = getIntInput();
                if (c == 1) continue;
                return;
            }

            // Giả định InventoryItem có constructor/setter chấp nhận reorderThreshold
            InventoryItem item = new InventoryItem(id, name, unit, quantity, InventoryStatus.IN_STOCK);
            try {
                getInventoryService().create(item);
                
                // 💾 Tự động lưu vào inventory.csv
                MainApp.autoSave();
                
                System.out.println("✅ Thêm hàng tồn kho thành công! ID: " + id);
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
            }

            System.out.println("\n🔁 1. Thêm tiếp");
            System.out.println("   0. Quay lại danh sách");
            System.out.print("👉 Chọn: ");
            int next = getIntInput();
            if (next == 1) continue;
            return;
        }
    }

    //  ==== Sửa =====
    private static void updateInventoryItem() {
        while (true) {
            System.out.print("\n✏️  SỬA - Nhập mã (gõ 0 để quay lại): ");
            String id = getStringInput();
            if (id == null) id = "";
            if ("0".equals(id)) return;

            if (id.isEmpty()) {
                System.out.println("❌ Mã không được để trống.");
                continue;
            }

            var opt = getInventoryService().getById(id);
            if (opt.isEmpty()) {
                System.out.println("❌ Không tìm thấy với mã: " + id);
                System.out.println("\n🔁 1. Nhập lại | 0. Quay lại");
                System.out.print("👉 Chọn: ");
                if (getIntInput() == 0) return;
                continue;
            }

            InventoryItem item = opt.get();
            System.out.println("\n📋 Thông tin hiện tại:");
            System.out.println("   Mã: " + item.getStockItemId());
            System.out.println("   Tên: " + item.getName());
            System.out.println("   Đơn vị: " + item.getUnit());
            System.out.println("   Tồn kho: " + item.getQuantityOnHand());
            System.out.println("   Trạng thái: " + item.getStatus());

            System.out.print("\nTên mới (Enter để giữ nguyên): ");
            String newName = getStringInput();
            if (!newName.isEmpty()) {
                item.setName(newName);
            }

            System.out.println("\n🧾 ĐƠN VỊ MỚI (Enter để giữ nguyên):");
            System.out.println(" 1. PCS | 2. BOX | 3. PACK | 4. KG | 5. G | 6. LITER | 7. ML");
            System.out.print("👉 Chọn (1-7): ");
            int unitChoice = getIntInput();
            if (unitChoice >= 1 && unitChoice <= Unit.values().length) {
                item.setUnit(Unit.values()[unitChoice - 1]);
            }

            System.out.print("Số lượng tồn mới (Enter để giữ nguyên): ");
            String qtyStr = getStringInput();
            if (!qtyStr.isEmpty()) {
                try {
                    double newQty = Double.parseDouble(qtyStr);
                    if (newQty >= 0) {
                        item.setQuantityOnHand(newQty);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Số lượng không hợp lệ, giữ nguyên.");
                }
            }

            System.out.println("\n📊 TRẠNG THÁI (Enter để giữ nguyên):");
            System.out.println(" 1. OUT_OF_STOCK | 2. LOW_STOCK | 3. IN_STOCK | 4. INACTIVE");
            System.out.print("👉 Chọn (1-4): ");
            int statusChoice = getIntInput();
            if (statusChoice >= 1 && statusChoice <= InventoryStatus.values().length) {
                item.setStatus(InventoryStatus.values()[statusChoice - 1]);
            }

            try {
                getInventoryService().update(item);
                MainApp.autoSave();
                System.out.println("✅ Cập nhật thành công!");
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
            }

            System.out.println("\n🔁 1. Sửa tiếp | 0. Quay lại");
            System.out.print("👉 Chọn: ");
            if (getIntInput() != 1) return;
            continue;
        }
    }

    //  ==== Xóa nguyên liệu =====
    private static void deleteInventoryItem() {
        while (true) {
            System.out.print("\n🗑️  XÓA - Nhập mã (gõ 0 để quay lại): ");
            String id = getStringInput();
            if (id == null) id = "";
            if ("0".equals(id)) return;

            if (id.isEmpty()) {
                System.out.println("❌ Mã không được để trống.");
                continue;
            }

            var opt = getInventoryService().getById(id);
            if (opt.isEmpty()) {
                System.out.println("❌ Không tìm thấy với mã: " + id);
                System.out.println("\n🔁 1. Nhập lại | 0. Quay lại");
                System.out.print("👉 Chọn: ");
                if (getIntInput() == 0) return;
                continue;
            }

            InventoryItem item = opt.get();
            System.out.println("\n⚠️  Xác nhận xóa:");
            System.out.println("   Mã: " + item.getStockItemId());
            System.out.println("   Tên: " + item.getName());
            System.out.println("   Tồn kho: " + item.getQuantityOnHand() + " " + item.getUnit());
            System.out.print("\n❓ Bạn có chắc chắn muốn xóa? (Y/N): ");
            String confirm = getStringInput().toUpperCase();

            if (!confirm.equals("Y")) {
                System.out.println("↩️ Hủy xóa.");
                return;
            }

            try {
                boolean success = getInventoryService().delete(id);
                if (success) {
                    MainApp.autoSave();
                    System.out.println("✅ Xóa thành công!");
                } else {
                    System.out.println("❌ Không thể xóa.");
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
            }

            System.out.println("\n🔁 1. Xóa tiếp | 0. Quay lại");
            System.out.print("👉 Chọn: ");
            if (getIntInput() != 1) return;
            continue;
        }
    }

    //  ==== Tìm kiếm =====
    private static void searchInventoryItem() {
        while (true) {
            System.out.println("\n🔍 TÌM KIẾM");
            System.out.print("Nhập mã hoặc tên (gõ 0 để quay lại): ");
            String keyword = getStringInput();
            if (keyword == null) keyword = "";
            if ("0".equals(keyword)) return;

            if (keyword.isEmpty()) {
                System.out.println("❌ Từ khóa không được để trống.");
                continue;
            }

            // Tìm theo ID
            var byId = getInventoryService().getById(keyword);
            if (byId.isPresent()) {
                InventoryItem item = byId.get();
                System.out.println("\n✅ Tìm thấy theo mã:");
                System.out.println(repeat("─", 102));
                System.out.printf("%-10s %-30s %-13s %15s %-15s%n",
                    "Mã", "Tên", "Đơn vị", "Tồn kho", "Trạng thái");
                System.out.println(repeat("─", 102));
                System.out.printf("%-10s %-30s %-13s %,15.2f %-15s%n",
                    item.getStockItemId(), item.getName(), item.getUnit(),
                    item.getQuantityOnHand(), item.getStatus());
                pause();
                System.out.println("\n🔁 1. Tìm tiếp | 0. Quay lại");
                System.out.print("👉 Chọn: ");
                if (getIntInput() != 1) return;
                continue;
            }

            // Tìm theo tên (chứa keyword)
            String finalKeyword = keyword.toLowerCase();
            List<InventoryItem> results = getInventoryService().getAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(finalKeyword))
                .collect(Collectors.toList());

            if (results.isEmpty()) {
                System.out.println("❌ Không tìm thấy kết quả nào.");
            } else {
                System.out.println("\n✅ Tìm thấy " + results.size() + " kết quả:");
                System.out.println(repeat("─", 102));
                System.out.printf("%-10s %-30s %-13s %15s %-15s%n",
                    "Mã", "Tên", "Đơn vị", "Tồn kho", "Trạng thái");
                System.out.println(repeat("─", 102));
                for (InventoryItem item : results) {
                    System.out.printf("%-10s %-30s %-13s %,15.2f %-15s%n",
                        item.getStockItemId(), item.getName(), item.getUnit(),
                        item.getQuantityOnHand(), item.getStatus());
                }
                pause();
            }

            System.out.println("\n🔁 1. Tìm tiếp | 0. Quay lại");
            System.out.print("👉 Chọn: ");
            if (getIntInput() != 1) return;
        }
    }
}