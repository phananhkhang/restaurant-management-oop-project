package service;

import model.Ingredient;
import model.enums.Unit;
import repository.IngredientRepository;

import java.util.List;

/**
 * Lớp Service (Nghiệp vụ) cho Ingredient.
 * Chịu trách nhiệm xử lý logic, validation và gọi đến Repository.
 */
public class IngredientService {

    // Service phụ thuộc vào Repository để lấy dữ liệu
    private final IngredientRepository ingredientRepository;

    /**
     * Constructor: Khởi tạo repository.
     */
    public IngredientService() {
        this.ingredientRepository = new IngredientRepository();
    }

    /**
     * Lấy tất cả nguyên liệu cho một món ăn (MenuItem) cụ thể.
     *
     * @param itemId Mã của món ăn (ví dụ: "M001")
     * @return Danh sách các nguyên liệu
     */
    public List<Ingredient> getIngredientsByItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            // Trả về danh sách rỗng nếu itemId không hợp lệ
            return List.of(); 
        }
        return ingredientRepository.findByItemId(itemId);
    }

    /**
     * Thêm một nguyên liệu mới cho một món ăn.
     *
     * @param itemId Mã món ăn
     * @param name Tên nguyên liệu
     * @param quantity Số lượng
     * @param unit Đơn vị
     * @return Đối tượng Ingredient vừa được tạo
     * @throws IllegalArgumentException Nếu đầu vào không hợp lệ
     */
    public Ingredient addNewIngredient(String itemId, String name, double quantity, Unit unit) {
        // --- Validation (Kiểm tra logic nghiệp vụ) ---
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã món ăn (itemId) không được để trống.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được để trống.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Đơn vị không được để trống.");
        }

        // --- Logic ---
        // 1. Tạo ID mới
        String newId = ingredientRepository.generateNewId();

        // 2. Tạo đối tượng
        // (Lưu ý: thứ tự phải khớp với constructor của Ingredient)
        Ingredient newIngredient = new Ingredient(newId, name, unit, quantity, itemId);

        // 3. Gọi repository để lưu
        ingredientRepository.add(newIngredient);

        return newIngredient;
    }

    /**
     * Cập nhật thông tin của một nguyên liệu cụ thể.
     *
     * @param ingredientId ID của nguyên liệu cần cập nhật (ví dụ: "I005")
     * @param newName Tên mới
     * @param newQuantity Số lượng mới
     * @param newUnit Đơn vị mới
     * @return true nếu cập nhật thành công
     * @throws IllegalArgumentException nếu không tìm thấy nguyên liệu
     */
    public boolean updateIngredient(String ingredientId, String newName, double newQuantity, Unit newUnit) {
        // --- Validation ---
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được để trống.");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        if (newUnit == null) {
            throw new IllegalArgumentException("Đơn vị không được để trống.");
        }

        // --- Logic ---
        // 1. Tìm đối tượng cũ (Sử dụng phương thức ta vừa thêm vào Repo)
        Ingredient existingIngredient = ingredientRepository.findById(ingredientId);

        if (existingIngredient == null) {
            throw new IllegalArgumentException("Không tìm thấy nguyên liệu với ID: " + ingredientId);
        }

        // 2. Cập nhật các trường
        existingIngredient.setName(newName);
        existingIngredient.setQuantity(newQuantity);
        existingIngredient.setUnit(newUnit);

        // 3. Gọi repository để lưu (phương thức update sẽ tìm và ghi đè)
        ingredientRepository.update(existingIngredient);
        return true;
    }

    /**
     * Xóa MỘT nguyên liệu cụ thể.
     *
     * @param ingredientId ID của nguyên liệu cần xóa (ví dụ: "I005")
     * @return true nếu xóa thành công, false nếu không
     */
    public boolean deleteIngredient(String ingredientId) {
        if (ingredientId == null || ingredientId.trim().isEmpty()) {
            return false;
        }
        // Sử dụng phương thức ta vừa thêm vào Repo
        return ingredientRepository.delete(ingredientId);
    }

    /**
     * Xóa TẤT CẢ nguyên liệu của một món ăn (ví dụ: khi xóa món ăn đó).
     *
     * @param itemId Mã của món ăn
     */
    public void deleteAllIngredientsByItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            return;
        }
        ingredientRepository.deleteByItemId(itemId);
    }
}