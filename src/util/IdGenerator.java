package util;

import java.util.List;
import java.util.function.Function;

/**
 * IdGenerator - Tự động tạo ID cho các entity
 */
public class IdGenerator {
    public static <T> String generateId(String prefix, List<T> existingItems, Function<T, String> idExtractor) {
        int maxNumber = 0;
        
        // Tìm số lớn nhất trong danh sách hiện có
        for (T item : existingItems) {
            String id = idExtractor.apply(item);
            if (id != null && id.startsWith(prefix)) {
                try {
                    String numberPart = id.substring(prefix.length());
                    int number = Integer.parseInt(numberPart);
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua ID không đúng format
                }
            }
        }
        
        // Tạo ID mới
        return prefix + String.format("%03d", maxNumber + 1);
    }
    /**
     * Tạo ID cho Staff (S001, S002, ...)
     */
    public static String generateStaffId(List<model.Staff> existingStaff) {
        return generateId("S", existingStaff, model.Staff::getId);
    }
    
    /**
     * Tạo ID cho MenuItem (M001, M002, ...)
     */
    public static String generateMenuItemId(List<model.MenuItem> existingItems) {
        return generateId("M", existingItems, model.MenuItem::getItemId);
    }
    
    /**
     * Tạo ID cho TableEntity (T001, T002, ...)
     */
    public static String generateTableId(List<model.Table> existingTables) {
        return generateId("T", existingTables, model.Table::getTableId);
    }
    
    /**
     * Tạo ID cho InventoryItem (I001, I002, ...)
     */
    public static String generateInventoryId(List<model.InventoryItem> existingItems) {
        return generateId("I", existingItems, model.InventoryItem::getStockItemId);
    }
    
    /**
     * Tạo ID cho Payment (P001, P002, ...)
     */
    public static String generatePaymentId(List<model.Payment> existingPayments) {
        return generateId("P", existingPayments, model.Payment::getPaymentId);
    }
    
    /**
     * Tạo ID cho Bill (B001, B002, ...)
     */
    public static String generateBillId(List<model.Bill> existingBills) {
        return generateId("B", existingBills, model.Bill::getId);
    }
    
    /**
     * Tạo ID cho Expense (E001, E002, ...)
     */
    public static String generateExpenseId(List<model.Expense> existingExpenses) {
        return generateId("E", existingExpenses, model.Expense::getExpenseId);
    }
}
