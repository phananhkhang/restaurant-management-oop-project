package app.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import model.MenuItem;
import service.MenuItemService;
import model.enums.MenuType;
import util.IdGenerator;
import app.MainApp;
import util.Authentication;
import model.Ingredient;
import model.enums.Unit;
import service.IngredientService;


public class MenuItemUI {
    // ===== KHỞI TẠO =====
    private static final Scanner scanner = new Scanner(System.in);
    
    private static MenuItemService getMenuService() {
        return MainApp.getMenuService();
    }

    // ===== PHƯƠNG THỨC TIỆN ÍCH =====

    private static IngredientService getIngredientService() {
        // (Giả định MainApp có hàm getter này)
        return MainApp.getIngredientService(); 
    }


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

    // ===== MENU THỰC ĐƠN =====

    public static void menuItemMenu() {
        // Kiểm tra quyền quản lý thực đơn
        if (!Authentication.canManageMenu()) {
            System.out.println("❌ Bạn không có quyền truy cập chức năng này!");
            System.out.println("   Chỉ MANAGER và CHEF mới có thể quản lý thực đơn.");
            pause();
            return;
        }
        
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║                🍽️  QUẢN LÝ THỰC ĐƠN                  ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. 📋 Xem danh sách & Quản lý món ăn");
            System.out.println(" 2.  Báo cáo thực đơn");
            System.out.println(" 3. 🧪 Quản lý nguyên liệu cho món");
            System.out.println(" 0. 🔙 Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: viewMenuListAndDetails(); break;
                case 2: getMenuService().printReport(); pause(); break;
                case 3: listAllMenuItems(); manageIngredientsForExistingItem(); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private static void listAllMenuItems() {
        List<MenuItem> items = getMenuService().getAll();
        System.out.println("\n📋 THỰC ĐƠN (" + items.size() + " món)");
        System.out.println(repeat("─", 100));
        System.out.printf("%-10s %-30s %-15s %-15s %-10s %s%n",
            "ID", "Tên món", "Danh mục", "Giá", "Trạng thái", "Thời gian");
        System.out.println(repeat("─", 100));
        
        for (MenuItem item : items) {
            System.out.printf("%-10s %-30s %-15s %,15.0f %-10s %d phút%n",
                item.getItemId(), item.getItemName(), item.getCategory(),
                item.getPrice(), item.isAvailable() ? "Còn" : "Hết", item.getPreparationTime());
        }
    }
    
    private static void viewMenuListAndDetails() {
        while (true) {
            listAllMenuItems();
            
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║            📋 QUẢN LÝ DANH SÁCH MÓN ĂN               ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println(" 1. 🔍 Tìm kiếm món ăn");
            System.out.println(" 2. 📊 Xem món theo danh mục");
            System.out.println(" 3. ➕ Thêm món mới");
            System.out.println(" 4. ✏️  Cập nhật món");
            System.out.println(" 5. 🗑️  Xóa món");
            System.out.println(" 0. 🔙 Quay lại");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    searchMenuItemSubmenu();
                    break;
                case 2:
                    listMenuItemsByCategory();
                    break;
                case 3:
                    addNewMenuItem();
                    break;
                case 4:
                    updateMenuItem();
                    break;
                case 5:
                    deleteMenuItem();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    private static void searchMenuItemSubmenu() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║                🔍 TÌM KIẾM MÓN ĂN                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println(" 1. 🔑 Tìm theo ID");
        System.out.println(" 2. 📝 Tìm theo tên");
        System.out.println(" 0. 🔙 Quay lại");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.print("👉 Chọn: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                findMenuItemById();
                break;
            case 2:
                searchMenuItemByName();
                break;
            case 0:
                return;
            default:
                System.out.println("❌ Lựa chọn không hợp lệ!");
                pause();
        }
    }

   private static void findMenuItemById() {
        System.out.print("\n🔍 Nhập ID món: ");
        String id = getStringInput();
        
        if (id == null || id.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập ID món!");
            pause();
            return;
        }
        
        Optional<MenuItem> item = getMenuService().getById(id);
        if (item.isPresent()) {
            MenuItem m = item.get();
            System.out.println("\n📋 CHI TIẾT MÓN ĂN");
            System.out.println(repeat("─", 50));
            System.out.println("ID:          " + m.getItemId());
            System.out.println("Tên món:     " + m.getItemName());
            System.out.println("Danh mục:    " + m.getCategory());
            System.out.printf("Giá:         %,.0f VNĐ%n", m.getPrice());
            System.out.println("Trạng thái:  " + (m.isAvailable() ? "Còn" : "Hết"));
            System.out.println("Thời gian:   " + m.getPreparationTime() + " phút");
            System.out.println("Mô tả:       " + m.getDescription());
            
            // ⬇️ PHẦN THÊM MỚI ⬇️
            System.out.println(repeat("─", 50));
            System.out.println("NGUYÊN LIỆU CẦN DÙNG:");
            
            List<Ingredient> ingredients = getIngredientService().getIngredientsByItemId(m.getItemId());
            
            if (ingredients.isEmpty()) {
                System.out.println("  (Món này chưa có nguyên liệu nào)");
            } else {
                for (Ingredient ing : ingredients) {
                    System.out.printf("  - %-20s: %.1f %s%n",
                            ing.getName(),
                            ing.getQuantity(),
                            ing.getUnit().getDisplayValue()
                    );
                }
            }
            // ⬆️ KẾT THÚC PHẦN THÊM MỚI ⬆️

        } else {
            System.out.println("❌ Không tìm thấy món với ID: " + id);
        }
        pause();
    }


    // 💥 THAY THẾ TOÀN BỘ HÀM NÀY 💥
    private static void addNewMenuItem() {
        System.out.println("\n➕ THÊM MÓN MỚI");
        
        String id = IdGenerator.generateMenuItemId(getMenuService().getAll());
        System.out.println("🆔 ID tự động: " + id);
        
        System.out.print("Tên món: ");
        String name = getStringInput();
        
        // Gọi hàm helper cho gọn
        MenuType category = selectMenuType(); 

        System.out.print("Giá: ");
        double price = getDoubleInput();
        System.out.print("Mô tả: ");
        String desc = getStringInput();
        System.out.print("Thời gian chế biến (phút): ");
        int time = getIntInput();
        
        MenuItem item = new MenuItem(id, name, category, price, desc, true, time);
        
        try {
            getMenuService().create(item);
            
            // 💾 Tự động lưu sau khi thêm món
            MainApp.autoSave();
            
            System.out.println("✅ Thêm món thành công!");
            
            // ⬇️ PHẦN THÊM MỚI ⬇️
            System.out.print("\n👉 Bạn có muốn thêm nguyên liệu cho món này? (y/n): ");
            if (getStringInput().equalsIgnoreCase("y")) {
                manageIngredientsMenu(item); // Gọi hàm quản lý
            }
            // ⬆️ KẾT THÚC PHẦN THÊM MỚI ⬆️
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
        pause();
    }
    
    private static void searchMenuItemByName() {
        System.out.print("\n🔍 Nhập tên món: ");
        String name = getStringInput();
        
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Vui lòng nhập tên món!");
            pause();
            return;
        }
        
        List<MenuItem> items = getMenuService().searchByName(name);
        if (items.isEmpty()) {
            System.out.println("❌ Không tìm thấy món với tên: " + name);
        } else {
            System.out.println("\n📋 Tìm thấy " + items.size() + " món:");
            items.forEach(m -> System.out.printf("  - %s: %s - %,.0f VNĐ%n",
                m.getItemId(), m.getItemName(), m.getPrice()));
        }
        pause();
    }
    
    private static void listMenuItemsByCategory() {
        System.out.println("\nDanh mục:");
        int i = 1;
        for (MenuType cat : MenuType.values()) {
            System.out.println(i++ + ". " + cat);
        }
        System.out.print("Chọn danh mục: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < MenuType.values().length) {
            MenuType category = MenuType.values()[choice];
            List<MenuItem> items = getMenuService().findByCategory(category);
            System.out.println("\n📋 DANH MỤC: " + category + " (" + items.size() + " món)");
            items.forEach(m -> System.out.printf("  - %s: %s - %,.0f VNĐ%n",
                m.getItemId(), m.getItemName(), m.getPrice()));
        }
        pause();
    }
    
    // ===== BẮT ĐẦU SỬA LỖI 1 =====
    // HÀM `updateMenuItem` CŨ (DÒNG 284) ĐÃ BỊ XÓA
    // ===== KẾT THÚC SỬA LỖI 1 =====
    
   // 💥 THAY THẾ TOÀN BỘ HÀM NÀY 💥
    private static void deleteMenuItem() {
        System.out.print("\n🗑️  Nhập ID món cần xóa: ");
        String id = getStringInput();
        
        System.out.print("⚠️  Xác nhận xóa món ăn này? (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            
            if (getMenuService().delete(id)) {
                System.out.println("✅ Xóa món ăn thành công!");
                
                // ⬇️ PHẦN THÊM MỚI ⬇️
                getIngredientService().deleteAllIngredientsByItemId(id);
                System.out.println("... và đã xóa các nguyên liệu liên quan.");
                // ⬆️ KẾT THÚC PHẦN THÊM MỚI ⬆️
                
                // 💾 Tự động lưu sau khi xóa món
                MainApp.autoSave();
                
            } else {
                System.out.println("❌ Không tìm thấy món ăn!");
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
        pause();
    }


// ==========================================================
    //       HÀM HELPER CHO QUẢN LÝ NGUYÊN LIỆU
    // ==========================================================

    /**
     * Hàm mới (được gọi từ case 8)
     * Bước 1: Yêu cầu người dùng chọn một món ăn
     */
    private static void manageIngredientsForExistingItem() {
        System.out.print("\n✏️ Nhập ID món ăn bạn muốn quản lý nguyên liệu: ");
        String itemId = getStringInput();

        Optional<MenuItem> itemOpt = getMenuService().getById(itemId);
        if (itemOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy món ăn với ID: " + itemId);
            pause();
            return;
        }

        // Nếu tìm thấy, chuyển đến menu quản lý
        manageIngredientsMenu(itemOpt.get());
    }

    /**
     * Hàm mới (trung tâm)
     * Bước 2: Hiển thị menu Thêm/Sửa/Xóa nguyên liệu cho món ăn đã chọn
     */
    private static void manageIngredientsMenu(MenuItem item) {
        String itemId = item.getItemId();
        
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════╗");
            System.out.printf("║ 🌿 QUẢN LÝ NGUYÊN LIỆU CHO: %-16s ║%n", item.getItemName());
            System.out.println("╚═══════════════════════════════════════════╝");
            
            // Luôn hiển thị danh sách nguyên liệu hiện tại
            listIngredientsForItem(itemId); 
            
            System.out.println("\n 1. Thêm nguyên liệu mới");
            System.out.println(" 2. Sửa nguyên liệu");
            System.out.println(" 3. Xóa nguyên liệu");
            System.out.println(" 0. Quay lại");
            System.out.println("═════════════════════════════════════════════");
            System.out.print("👉 Chọn: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1: addIngredient(itemId); break;
                case 2: updateIngredient(itemId); break; // <-- Sẽ gọi hàm mới (ở dòng 433)
                case 3: deleteIngredient(itemId); break;
                case 0: return;
                default: System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    /**
     * Hàm mới (helper)
     * Chỉ hiển thị danh sách nguyên liệu cho một món
     */
    private static void listIngredientsForItem(String itemId) {
        List<Ingredient> ingredients = getIngredientService().getIngredientsByItemId(itemId);
        
        if (ingredients.isEmpty()) {
            System.out.println("  (Món này chưa có nguyên liệu nào)");
            return;
        }
        
        System.out.println("  Danh sách nguyên liệu hiện tại:");
        System.out.println(repeat("─", 45));
        System.out.printf("  %-8s %-20s %s%n", "ID", "Tên", "Số lượng");
        System.out.println(repeat("─", 45));
        for (Ingredient ing : ingredients) {
            System.out.printf("  %-8s %-20s %.1f %s%n",
                    ing.getIngredientId(),
                    ing.getName(),
                    ing.getQuantity(),
                    ing.getUnit().getDisplayValue()
            );
        }
        System.out.println(repeat("─", 45));
    }

    /**
     * Hàm mới (helper)
     * Logic thêm nguyên liệu
     */
    private static void addIngredient(String itemId) {
        System.out.println("\n➕ THÊM NGUYÊN LIỆU MỚI");
        System.out.print("Tên nguyên liệu: ");
        String name = getStringInput();
        System.out.print("Số lượng: ");
        double quantity = getDoubleInput();
        Unit unit = selectUnit(); // Gọi hàm chọn đơn vị

        try {
            getIngredientService().addNewIngredient(itemId, name, quantity, unit);
            System.out.println("✅ Thêm thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
    }

    // ===== BẮT ĐẦU SỬA LỖI 2 =====
    // THÊM HÀM `updateIngredient` BỊ THIẾU
    /**
     * Hàm mới (helper)
     * Logic sửa nguyên liệu
     */
    private static void updateIngredient(String itemId) {
        System.out.println("\n✏️ SỬA NGUYÊN LIỆU");
        System.out.print("Nhập ID nguyên liệu cần sửa (ví dụ: I001): ");
        String ingredientId = getStringInput();

        // Tìm nguyên liệu
        Optional<Ingredient> ingOpt = getIngredientService().getIngredientsByItemId(itemId).stream()
                .filter(i -> i.getIngredientId().equalsIgnoreCase(ingredientId))
                .findFirst();

        if (ingOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy nguyên liệu với ID: " + ingredientId + " cho món này.");
            pause();
            return;
        }

        Ingredient ingredient = ingOpt.get();
        System.out.println("Đang sửa: " + ingredient.getName());

        // Lấy giá trị mới
        System.out.print("Số lượng mới (hiện tại: " + ingredient.getQuantity() + ", nhập 0 để giữ nguyên): ");
        double newQuantity = getDoubleInput();
        if (newQuantity > 0) {
            ingredient.setQuantity(newQuantity);
            System.out.println("Đã cập nhật số lượng.");
        } else {
            System.out.println("Số lượng không đổi.");
        }

        System.out.println("Đơn vị mới (hiện tại: " + ingredient.getUnit().getDisplayValue() + ")");
        System.out.print("Bạn có muốn đổi đơn vị? (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            ingredient.setUnit(selectUnit()); // Sử dụng helper
            System.out.println("Đã cập nhật đơn vị.");
        }

        // Gọi service để lưu
       try {
    // Sửa lỗi: Gọi hàm update với đủ các tham số
    getIngredientService().updateIngredient(
        ingredient.getIngredientId(),
        ingredient.getName(),
        ingredient.getQuantity(),
        ingredient.getUnit()
    ); 
    System.out.println("✅ Cập nhật nguyên liệu thành công!");
} catch (Exception e) {
            System.out.println("❌ Lỗi khi cập nhật: " + e.getMessage());
        }
        pause();
    }
    // ===== KẾT THÚC SỬA LỖI 2 =====


    // Đây là hàm updateMenuItem MỚI (được giữ lại)
    private static void updateMenuItem() {
        System.out.print("\n✏️  Nhập ID món cần sửa: ");
        String id = getStringInput();
        
        Optional<MenuItem> opt = getMenuService().getById(id);
        if (opt.isEmpty()) {
            System.out.println("❌ Không tìm thấy!");
            pause();
            return;
        }
        
        MenuItem m = opt.get();
        
        // Vòng lặp menu cập nhật
        while(true) {
            System.out.println("\n✏️ BẠN ĐANG SỬA MÓN: " + m.getItemName());
            System.out.println(repeat("─", 50));
            System.out.println(" 1. Sửa tên:      " + m.getItemName());
            System.out.println(" 2. Sửa danh mục: " + m.getCategory());
            System.out.printf(" 3. Sửa giá:      %,.0f VNĐ%n", m.getPrice());
            System.out.println(" 4. Sửa mô tả:    " + m.getDescription());
            System.out.println(" 5. Sửa thời gian: " + m.getPreparationTime() + " phút");
            System.out.println(" 6. Sửa trạng thái: " + (m.isAvailable() ? "Còn" : "Hết"));
            System.out.println(" 0. Lưu và Thoát");
            System.out.println(repeat("─", 50));
            System.out.print("👉 Chọn thuộc tính cần sửa: ");

            int choice = getIntInput();
            
            switch (choice) {
                case 1:System.out.print("Tên mới: ");m.setItemName(getStringInput());break;
                case 2:m.setCategory(selectMenuType());break;
                case 3:System.out.print("Giá mới: ");m.setPrice(getDoubleInput());break;
                case 4:System.out.print("Mô tả mới: ");m.setDescription(getStringInput());break;
                case 5:System.out.print("Thời gian mới (phút): ");m.setPreparationTime(getIntInput());break;
                case 6:System.out.print("Trạng thái mới? (1 = Còn, 0 = Hết): ");m.setAvailable(getIntInput() == 1);break;
                case 0:
                    // Lưu thay đổi vào file
                    getMenuService().update(m);
                    
                    // 💾 Tự động lưu sau khi cập nhật món
                    MainApp.autoSave();
                    
                    System.out.println("✅ Cập nhật thành công!");
                    pause();
                    return; // Thoát khỏi vòng lặp
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    /**
     * Hàm mới (helper)
     * Logic xóa nguyên liệu
     */
    private static void deleteIngredient(String itemId) {
        System.out.println("\n🗑️ XÓA NGUYÊN LIỆU");
        System.out.print("Nhập ID nguyên liệu cần xóa (ví dụ: I001): ");
        String ingredientId = getStringInput();

        Optional<Ingredient> ingOpt = getIngredientService().getIngredientsByItemId(itemId).stream()
                .filter(i -> i.getIngredientId().equalsIgnoreCase(ingredientId))
                .findFirst();

        if (ingOpt.isEmpty()) {
            System.out.println("❌ Không tìm thấy nguyên liệu với ID: " + ingredientId + " cho món này.");
            return;
        }

        System.out.print("⚠️  Xác nhận xóa '" + ingOpt.get().getName() + "'? (y/n): ");
        if (getStringInput().equalsIgnoreCase("y")) {
            if (getIngredientService().deleteIngredient(ingredientId)) {
                System.out.println("✅ Xóa thành công!");
            } else {
                System.out.println("❌ Lỗi! Không thể xóa.");
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    /**
     * Hàm mới (helper)
     * Hiển thị danh sách Unit (enum) cho người dùng chọn
     */
    private static Unit selectUnit() {
        System.out.println("Chọn đơn vị:");
        Unit[] units = Unit.values(); 
        for (int i = 0; i < units.length; i++) {
            System.out.printf("  %d. %s (%s)%n", i + 1, units[i].name(), units[i].getDisplayValue());
        }

        while (true) {
            System.out.print("Chọn (1-" + units.length + "): ");
            int choice = getIntInput();
            if (choice > 0 && choice <= units.length) {
                return units[choice - 1];
            }
            System.out.println("❌ Lựa chọn không hợp lệ!");
        }
    }

    /**
     * Hàm mới (helper)
     * Tách logic chọn MenuType ra cho gọn
     */
    private static MenuType selectMenuType() {
        System.out.println("Danh mục:");
        MenuType[] categories = MenuType.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, categories[i]);
        }

        while (true) {
            System.out.print("Chọn (1-" + categories.length + "): ");
            int choice = getIntInput();
            if (choice > 0 && choice <= categories.length) {
                return categories[choice - 1];
            }
            System.out.println("❌ Lựa chọn không hợp lệ!");
        }
    }
}