package app.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import model.Bill;
import model.MenuItem;
import model.Table;
import model.common.Money;
import service.MenuItemService;
import service.TableEntityService;
import model.enums.BillStatus;
import model.enums.TableStatus;
import util.IdGenerator;
import app.MainApp;
import util.Authentication;

public class TableUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);
    
    private static TableEntityService getTableService() {
        return MainApp.getTableService();
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

    // ===== MENU BÀN =====

    public static void tableMenu() {
        // Kiểm tra quyền quản lý bàn
        if (!Authentication.canManageTables()) {
            System.out.println("❌ Bạn không có quyền truy cập chức năng này!");
            System.out.println("   Chỉ MANAGER, WAITER và HOST mới có thể quản lý bàn.");
            pause();
            return;
        }
        
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           🪑 QUẢN LÝ BÀN                              ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. Xem danh sách bàn & Quản lý");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: viewTablesWithManagement(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private static void viewTablesWithManagement() {
        while (true) {
            listAllTables();
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println(" 1. 👁️  Xem bàn trống");
            System.out.println(" 2. 🔍 Tìm kiếm bàn");
            System.out.println(" 3. 🪑 Chiếm bàn (khách đến ngồi)");
            System.out.println(" 4. 🍽️  Gọi món cho bàn");
            System.out.println(" 5. ➕ Thêm bàn mới");
            System.out.println(" 6. ✏️  Cập nhật thông tin bàn");
            System.out.println(" 7. 🗑️  Xóa bàn");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: listAvailableTables(); break;
                case 2: searchTableMenu(); break;
                case 3: occupyTable(); break;
                case 4: orderForTable(); break;
                case 5: addNewTable(); break;
                case 6: updateTable(); break;
                case 7: deleteTable(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!"); pause();
            }
        }
    }
    
    private static void listAllTables() {
        List<Table> tables = getTableService().getAll();
        System.out.println("\n📋 DANH SÁCH BÀN (" + tables.size() + ")");
        System.out.println(repeat("─", 60));
        System.out.printf("%-15s %-30s %-15s%n",
            "Mã bàn", "Tên bàn", "Trạng thái");
        System.out.println(repeat("─", 60));
        
        int availableCount = 0;
        int occupiedCount = 0;
        
        for (Table table : tables) {
            String status = table.getStatus().toString();
            String statusDisplay = status.equals("AVAILABLE") ? "🟢 TRỐNG" : "🔴 ĐANG SỬ DỤNG";
            
            System.out.printf("%-15s %-30s %-15s%n",
                table.getTableId(), 
                table.getTableName(), 
                statusDisplay);
            
            if (status.equals("AVAILABLE")) {
                availableCount++;
            } else {
                occupiedCount++;
            }
        }
        
        System.out.println(repeat("─", 60));
        System.out.printf("Tổng kết: 🟢 Trống: %d bàn  |  🔴 Đang sử dụng: %d bàn%n", 
            availableCount, occupiedCount);
    }
    
    private static void listAvailableTables() {
        List<Table> tables = getTableService().getAvailableTables();
        System.out.println("\n🟢 BÀN TRỐNG (" + tables.size() + " bàn)");
        System.out.println(repeat("─", 50));
        
        if (tables.isEmpty()) {
            System.out.println("❌ Hiện tại không có bàn trống!");
        } else {
            for (Table t : tables) {
                System.out.printf("  🪑 %s: %s%n",
                    t.getTableId(), t.getTableName());
            }
        }
        System.out.println(repeat("─", 50));
        pause();
    }
    
    private static void searchTableMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           🔍 TÌM KIẾM BÀN                             ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. 🔑 Tìm kiếm theo mã bàn");
            System.out.println(" 2. 📝 Tìm kiếm theo tên bàn");
            System.out.println(" 0. Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: searchTableById(); break;
                case 2: searchTableByName(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!"); pause();
            }
        }
    }
    
    private static void searchTableById() {
        System.out.print("\n🔑 Nhập mã bàn cần tìm: ");
        String id = getStringInput();
        
        if (id.isEmpty()) {
            System.out.println("❌ Mã bàn không được để trống!");
            pause();
            return;
        }
        
        Optional<Table> result = getTableService().getById(id);
        
        if (result.isPresent()) {
            Table table = result.get();
            System.out.println("\n✅ TÌM THẤY BÀN:");
            System.out.println(repeat("─", 60));
            System.out.printf("%-15s: %s%n", "Mã bàn", table.getTableId());
            System.out.printf("%-15s: %s%n", "Tên bàn", table.getTableName());
            String statusDisplay = table.getStatus().toString().equals("AVAILABLE") ? "🟢 TRỐNG" : "🔴 ĐANG SỬ DỤNG";
            System.out.printf("%-15s: %s%n", "Trạng thái", statusDisplay);
            System.out.println(repeat("─", 60));
        } else {
            System.out.println("❌ Không tìm thấy bàn với mã: " + id);
        }
        
        pause();
    }
    
    private static void searchTableByName() {
        System.out.print("\n📝 Nhập tên bàn cần tìm: ");
        String name = getStringInput();
        
        List<Table> results = getTableService().searchByName(name);
        
        System.out.println("\n🔍 KẾT QUẢ TÌM KIẾM (" + results.size() + " bàn)");
        System.out.println(repeat("─", 60));
        
        if (results.isEmpty()) {
            System.out.println("❌ Không tìm thấy bàn nào phù hợp!");
        } else {
            System.out.printf("%-15s %-30s %-15s%n",
                "Mã bàn", "Tên bàn", "Trạng thái");
            System.out.println(repeat("─", 60));
            
            for (Table table : results) {
                String statusDisplay = table.getStatus().toString().equals("AVAILABLE") ? "🟢 TRỐNG" : "🔴 ĐANG SỬ DỤNG";
                System.out.printf("%-15s %-30s %-15s%n",
                    table.getTableId(), 
                    table.getTableName(), 
                    statusDisplay);
            }
        }
        
        System.out.println(repeat("─", 60));
        pause();
    }
    
    private static void occupyTable() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           🪑 CHIẾM BÀN (KHÁCH ĐẾN NGỒI)               ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        System.out.print("\n🔑 Nhập mã bàn cần chiếm: ");
        String tableId = getStringInput();
        
        if (tableId.isEmpty()) {
            System.out.println("❌ Mã bàn không được để trống!");
            pause();
            return;
        }
        
        // Kiểm tra bàn có tồn tại không
        Optional<Table> tableOpt = getTableService().getById(tableId);
        if (tableOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy bàn với mã: " + tableId);
            pause();
            return;
        }
        
        Table table = tableOpt.get();
        
        // Kiểm tra bàn đã bị chiếm chưa
        if (table.getStatus() != TableStatus.AVAILABLE) {
            System.out.println("❌ Bàn này đang được sử dụng! Không thể chiếm.");
            pause();
            return;
        }
        
        try {
            // 1. Chuyển trạng thái bàn sang OCCUPIED
            getTableService().occupyTable(tableId);
            
            // 2. Tạo hóa đơn mới gắn với bàn này
            Bill newBill = new Bill();
            
            // Sinh ID cho hóa đơn theo format B001, B002, ...
            String billId = IdGenerator.generateBillId(MainApp.getBillService().getAll());
            newBill.setId(billId);
            
            newBill.setTableId(tableId);
            newBill.setStatus(BillStatus.UNPAID);  // Trạng thái chưa thanh toán
            newBill.setNote("Hóa đơn cho bàn " + table.getTableName());
            
            // Lưu hóa đơn
            MainApp.getBillService().create(newBill);
            
            // 💾 Tự động lưu sau khi chiếm bàn
            MainApp.autoSave();
            
            System.out.println("\n✅ CHIẾM BÀN THÀNH CÔNG!");
            System.out.println("   🪑 Bàn: " + table.getTableName() + " (" + tableId + ")");
            System.out.println("   🧾 Hóa đơn: " + newBill.getId());
            System.out.println("   📊 Trạng thái bàn: ĐANG SỬ DỤNG");
            System.out.println("   💰 Trạng thái hóa đơn: CHƯA THANH TOÁN");
            System.out.println("\n💡 Bây giờ có thể gọi món cho bàn này!");
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void addNewTable() {
        System.out.println("\n➕ THÊM BÀN MỚI");
        
        String id = IdGenerator.generateTableId(getTableService().getAll());
        System.out.println("🆔 ID tự động: " + id);
        
        System.out.print("Tên bàn: ");
        String name = getStringInput();

        // Mặc định bàn mới là AVAILABLE (trống)
        Table table = new Table(id, name, TableStatus.AVAILABLE);
        
        try {
            getTableService().create(table);
            
            // 💾 Tự động lưu sau khi thêm bàn
            MainApp.autoSave();
            
            System.out.println("✅ Thêm bàn thành công! Bàn đang ở trạng thái TRỐNG.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void deleteTable() {
        System.out.print("\n🗑️  Nhập mã bàn cần xóa: ");
        String id = getStringInput();
        
        if (id == null || id.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập mã bàn!");
            pause();
            return;
        }
        
        // Kiểm tra bàn có tồn tại không
        Optional<Table> tableOpt = getTableService().getById(id);
        if (tableOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy bàn với mã: " + id);
            pause();
            return;
        }
        
        Table table = tableOpt.get();
        
        // Kiểm tra bàn có đang sử dụng không
        if (table.getStatus() == TableStatus.OCCUPIED) {
            System.out.println("❌ Không thể xóa! Bàn " + table.getTableId() + " đang được sử dụng.");
            System.out.println("   Vui lòng thanh toán và dọn bàn trước khi xóa.");
            pause();
            return;
        }
        
        // Hiển thị thông tin bàn
        System.out.println("\n📋 Thông tin bàn:");
        System.out.println("  Mã bàn: " + table.getTableId());
        System.out.println("  Tên bàn: " + table.getTableName());
        System.out.println("  Trạng thái: " + table.getStatus());
        
        System.out.print("\n❓ Xác nhận xóa bàn này? (y/n): ");
        String confirm = getStringInput();
        
        if (confirm != null && confirm.equalsIgnoreCase("y")) {
            try {
                getTableService().delete(id);
                
                // �💾 Tự động lưu sau khi xóa bàn
                MainApp.autoSave();
                
                System.out.println("✅ Xóa bàn thành công!");
            } catch (Exception e) {
                System.out.println("❌ Lỗi: " + e.getMessage());
            }
        } else {
            System.out.println("↩️  Đã hủy thao tác xóa.");
        }
        pause();
    }
    
    private static void updateTable() {
        System.out.print("\n✏️  Nhập mã bàn cần cập nhật: ");
        String id = getStringInput();
        
        Optional<Table> opt = getTableService().getById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy bàn!");
            pause();
            return;
        }
        
        Table table = opt.get();
        System.out.println("\n📋 Thông tin hiện tại:");
        System.out.println("   Tên: " + table.getTableName());
        System.out.println("   Trạng thái: " + table.getStatus());
        
        System.out.print("\nTên bàn mới (Enter để giữ nguyên): ");
        String name = getStringInput();
        if (!name.isEmpty()) table.setTableName(name);
        
        getTableService().update(table);
        
        // 💾 Tự động lưu sau khi cập nhật bàn
        MainApp.autoSave();
        
        System.out.println("✅ Cập nhật bàn thành công!");
        pause();
    }
    
    // ===== GỌI MÓN CHO BÀN =====
    private static void orderForTable() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           🍽️  GỌI MÓN CHO BÀN                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        System.out.print("\n🔑 Nhập mã bàn: ");
        String tableId = getStringInput();
        
        if (tableId.isEmpty()) {
            System.out.println("❌ Mã bàn không được để trống!");
            pause();
            return;
        }
        
        // Kiểm tra bàn có tồn tại không
        Optional<Table> tableOpt = getTableService().getById(tableId);
        if (tableOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy bàn với mã: " + tableId);
            pause();
            return;
        }
        
        Table table = tableOpt.get();
        
        // Kiểm tra bàn có đang sử dụng không
        if (table.getStatus() == TableStatus.AVAILABLE) {
            System.out.println("❌ Bàn này đang trống!");
            System.out.println("💡 Vui lòng chiếm bàn trước khi gọi món.");
            pause();
            return;
        }
        
        // Tìm hóa đơn đang mở của bàn
        Optional<Bill> billOpt = MainApp.getBillService().getOpenBillForTable(tableId);
        
        Bill bill;
        if (billOpt.isEmpty()) {
            // Bàn đang ĐANG SỬ DỤNG nhưng không có hóa đơn → Tự động tạo hóa đơn mới
            System.out.println("⚠️  Bàn đang sử dụng nhưng chưa có hóa đơn!");
            System.out.println("🔄 Đang tự động tạo hóa đơn mới...");
            
            bill = new Bill();
            
            // Sinh ID cho hóa đơn theo format B001, B002, ...
            String billId = IdGenerator.generateBillId(MainApp.getBillService().getAll());
            bill.setId(billId);
            
            bill.setTableId(tableId);
            bill.setStatus(BillStatus.UNPAID);
            bill.setNote("Hóa đơn cho bàn " + table.getTableName());
            
            MainApp.getBillService().create(bill);
            
            // 💾 Tự động lưu sau khi tạo hóa đơn mới
            MainApp.autoSave();
            
            System.out.println("✅ Đã tạo hóa đơn mới: " + bill.getId());
            System.out.println(repeat("─", 70));
        } else {
            bill = billOpt.get();
        }
        
        System.out.println("\n📋 BÀN: " + table.getTableName());
        System.out.println("🧾 HÓA ĐƠN: " + bill.getId());
        System.out.println(repeat("─", 70));
        
        // Hiển thị menu món ăn
        MenuItemService menuService = MainApp.getMenuService();
        List<MenuItem> availableItems = menuService.getAvailableItems();
        
        if (availableItems.isEmpty()) {
            System.out.println("❌ Hiện không có món nào!");
            pause();
            return;
        }
        
        System.out.println("\n📖 THỰC ĐƠN:");
        System.out.printf("%-8s %-30s %-15s%n", "Mã món", "Tên món", "Giá");
        System.out.println(repeat("─", 70));
        
        DecimalFormat df = new DecimalFormat("#,##0");
        for (MenuItem item : availableItems) {
            System.out.printf("%-8s %-30s %15s VND%n", 
                item.getItemId(), 
                item.getItemName(), 
                df.format(item.getPrice()));
        }
        
        System.out.println(repeat("─", 70));
        System.out.println("\n💡 Bạn có thể nhập nhiều mã món cùng lúc, cách nhau bởi khoảng trắng");
        System.out.println("   📌 Định dạng: MÃ_MÓN:SỐ_LƯỢNG (nếu không có số lượng, mặc định là 1)");
        System.out.println("   📝 Ví dụ 1: M001 M002 M003        (mỗi món 1 phần)");
        System.out.println("   📝 Ví dụ 2: M001:3 M002:2         (M001 có 3 phần, M002 có 2 phần)");
        System.out.println("   📝 Ví dụ 3: M001:2 M003 M005:4    (kết hợp cả hai)");
        System.out.print("\n🔑 Nhập mã món cần gọi (hoặc 0 để hủy): ");
        String input = getStringInput();
        
        if (input.equals("0")) {
            System.out.println("❌ Đã hủy!");
            pause();
            return;
        }
        
        // Tách chuỗi thành mảng các mã món
        String[] items = input.trim().split("\\s+");
        
        if (items.length == 0) {
            System.out.println("❌ Chưa nhập mã món!");
            pause();
            return;
        }
        
        System.out.println("\n📝 Đang xử lý " + items.length + " mục...");
        System.out.println(repeat("─", 70));
        
        int successCount = 0;
        BigDecimal totalAdded = BigDecimal.ZERO;
        
        // Xử lý từng món
        for (String item : items) {
            item = item.trim();
            if (item.isEmpty()) continue;
            
            // Tách mã món và số lượng (format: M001:3)
            String itemId;
            int quantity = 1; // Mặc định
            
            if (item.contains(":")) {
                String[] parts = item.split(":");
                itemId = parts[0].trim();
                
                // Parse số lượng
                try {
                    quantity = Integer.parseInt(parts[1].trim());
                    if (quantity <= 0) {
                        System.out.println("❌ Số lượng không hợp lệ cho " + itemId + " - Bỏ qua");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Số lượng không hợp lệ cho " + itemId + " - Bỏ qua");
                    continue;
                }
            } else {
                itemId = item;
            }
            
            Optional<MenuItem> itemOpt = menuService.getById(itemId);
            if (itemOpt.isEmpty()) {
                System.out.println("❌ Không tìm thấy món: " + itemId + " - Bỏ qua");
                continue;
            }
            
            MenuItem menuItem = itemOpt.get();
            
            try {
                // Thêm món vào bill
                String orderItem = String.format("%s x%d", 
                    menuItem.getItemName(), quantity, df.format(menuItem.getPrice()));
                bill.addItem(orderItem);
                
                // Cập nhật tổng tiền
                BigDecimal itemTotal = BigDecimal.valueOf(menuItem.getPrice() * quantity);
                totalAdded = totalAdded.add(itemTotal);
                
                // Hiển thị chi tiết với số lượng
                System.out.println("✅ " + itemId + " - " + menuItem.getItemName() + 
                    " x" + quantity + " (" + df.format(menuItem.getPrice()) + " VND) = " + 
                    df.format(itemTotal) + " VND");
                successCount++;
                
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi thêm món " + itemId + ": " + e.getMessage());
            }
        }
        
        // Cập nhật tổng tiền bill
        if (successCount > 0) {
            try {
                Money currentSubTotal = bill.getSubTotal();
                Money newSubTotal = currentSubTotal.add(new Money(totalAdded));
                bill.setSubTotal(newSubTotal);
                bill.recomputeTotal();
                
                // Lưu bill
                MainApp.getBillService().update(bill);
                
                // 💾 Tự động lưu sau khi order món
                MainApp.autoSave();
                
                System.out.println(repeat("─", 70));
                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println("║              ✅ HOÀN TẤT GỌI MÓN                      ║");
                System.out.println("╚═══════════════════════════════════════════════════════╝");
                System.out.println("   📊 Đã thêm: " + successCount + "/" + items.length + " mục");
                System.out.println("   💰 Tổng tiền thêm: " + df.format(totalAdded) + " VND");
                System.out.println("   💵 Tổng hóa đơn: " + df.format(bill.getTotalAmount().getAmount()) + " VND");
                
            } catch (Exception e) {
                System.out.println("❌ Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            }
        } else {
            System.out.println("\n⚠️  Không có món nào được thêm!");
        }
        
        pause();
    }
}
