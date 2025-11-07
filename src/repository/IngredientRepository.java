package repository;

import model.Ingredient;
import model.enums.Unit; // Cần import enum Unit

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Lớp Repository chịu trách nhiệm đọc và ghi dữ liệu Ingredient từ file CSV.
 * Lớp này quản lý file "ingredients.csv".
 */
public class IngredientRepository {

    // Đường dẫn đến file CSV. Bạn có thể thay đổi nếu cần.
    private final String FILE_PATH = "data/ingredients.csv";
    
    // Một danh sách (cache) chứa tất cả nguyên liệu để tăng tốc độ truy vấn
    private final List<Ingredient> ingredientsCache;

    /**
     * Constructor, khởi tạo cache và gọi loadFromFile() ngay lập-tức.
     */
    public IngredientRepository() {
        this.ingredientsCache = new ArrayList<>();
        loadFromFile(); // Tự động tải dữ liệu khi Repository được tạo
    }

    /**
     * Tải dữ liệu từ file CSV vào ingredientsCache.
     */
    private void loadFromFile() {
        File file = new File(FILE_PATH);

        // 1. Kiểm tra và tạo file/thư mục nếu chưa tồn tại
        if (!file.exists()) {
            try {
                // Tạo thư mục cha (ví dụ: "data")
                file.getParentFile().mkdirs();
                // Tạo file mới
                if (file.createNewFile()) {
                    // Ghi header vào file mới
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(String.join(",", new Ingredient().csvHeader()));
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                System.err.println("Lỗi nghiêm trọng: Không thể tạo file ingredients.csv: " + e.getMessage());
                return; // Không thể tiếp tục nếu không tạo được file
            }
        }

        // 2. Đọc dữ liệu từ file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine(); // Đọc và bỏ qua dòng header
            if (header == null) {
                System.out.println("File ingredients.csv rỗng.");
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Bỏ qua các dòng trống
                }

                String[] data = line.split(",");
                
                // Phải khớp với csvHeader(): {"ingredientId", "itemId", "name", "unit", "quantity"}
                if (data.length != 5) {
                    System.err.println("Cảnh báo: Bỏ qua dòng dữ liệu lỗi (không đủ cột): " + line);
                    continue;
                }

                try {
                    // Trích xuất dữ liệu từ mảng data[] theo đúng thứ tự cột
                    String ingredientId = data[0];
                    String itemId = data[1];
                    String name = data[2];
                    Unit unit = Unit.fromDisplayValue(data[3]); // Chuyển String "g" -> Unit.GRAM
                    double quantity = Double.parseDouble(data[4]);

                    // Tạo đối tượng Ingredient
                    // **LƯU Ý QUAN TRỌNG:**
                    // Thứ tự tham số ở đây phải khớp với Constructor của bạn:
                    // new Ingredient(ingredientId, name, unit, quantity, itemId)
                    Ingredient ingredient = new Ingredient(
                            ingredientId, // (id)
                            name,         // (name)
                            unit,         // (unit)
                            quantity,     // (quantity)
                            itemId        // (itemId)
                    );

                    this.ingredientsCache.add(ingredient);

                } catch (IllegalArgumentException e) {
                    // Lỗi khi gọi Unit.fromDisplayValue() (ví dụ: "gam" thay vì "g")
                    System.err.println("Cảnh báo: Bỏ qua dòng (lỗi đơn vị): " + line + " | " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file ingredients.csv: " + e.getMessage());
        }
    }

    /**
     * Ghi đè (overwrite) toàn bộ dữ liệu từ cache vào file CSV.
     */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) { // false để ghi đè
            // 1. Ghi header
            writer.write(String.join(",", new Ingredient().csvHeader()));
            writer.newLine();

            // 2. Ghi từng dòng dữ liệu từ cache
            for (Ingredient ingredient : this.ingredientsCache) {
                String[] csvRow = ingredient.toCsvRow();
                writer.write(String.join(",", csvRow));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Lỗi nghiêm trọng: Không thể lưu file ingredients.csv: " + e.getMessage());
        }
    }

    // --- CÁC PHƯƠNG THỨC PUBLIC CHO SERVICE ---

    /**
     * Tìm tất cả các nguyên liệu thuộc về một MenuItem.
     * Đây là hàm quan trọng nhất để liên kết 2 đối tượng.
     *
     * @param itemId Mã của MenuItem (ví dụ: "M001")
     * @return Một danh sách (List) các Ingredient
     */
    public List<Ingredient> findByItemId(String itemId) {
        // Sử dụng Stream API để lọc cache
        return this.ingredientsCache.stream()
                .filter(ingredient -> Objects.equals(ingredient.getItemId(), itemId))
                .collect(Collectors.toList());
    }
    
    /**
     * Thêm một nguyên liệu mới vào cache và lưu vào file.
     *
     * @param ingredient Đối tượng Ingredient đã có ID.
     */
    public void add(Ingredient ingredient) {
        if (ingredient == null) return;
        
        this.ingredientsCache.add(ingredient);
        saveToFile(); // Lưu thay đổi ra file
    }

    /**
     * Xóa tất cả nguyên liệu được liên kết với một itemId.
     * (Rất hữu ích khi bạn xóa một MenuItem).
     *
     * @param itemId Mã của MenuItem cần xóa nguyên liệu.
     */
    public void deleteByItemId(String itemId) {
        // Xóa tất cả các phần tử trong cache có itemId khớp
        boolean changed = this.ingredientsCache.removeIf(
                ingredient -> Objects.equals(ingredient.getItemId(), itemId)
        );

        if (changed) {
            saveToFile(); // Chỉ lưu file nếu có thay đổi
        }
    }
    
    /**
     * Cập nhật một nguyên liệu (tìm theo ID của nó).
     *
     * @param updatedIngredient Đối tượng nguyên liệu với thông tin đã cập nhật.
     */
    public void update(Ingredient updatedIngredient) {
        for (int i = 0; i < ingredientsCache.size(); i++) {
            Ingredient old = ingredientsCache.get(i);
            if (Objects.equals(old.getIngredientId(), updatedIngredient.getIngredientId())) {
                ingredientsCache.set(i, updatedIngredient); // Thay thế đối tượng cũ
                saveToFile();
                return; // Kết thúc
            }
        }
    }

    /**
     * Hàm helper để tạo ID mới cho nguyên liệu (ví dụ: "I001", "I002", ...)
     *
     * @return Một String ID mới chưa được sử dụng.
     */
    public String generateNewId() {
        int maxId = 0;
        for (Ingredient ing : ingredientsCache) {
            try {
                // Lấy phần số từ ID (ví dụ: "I005" -> 5)
                int idNum = Integer.parseInt(ing.getIngredientId().substring(1));
                if (idNum > maxId) {
                    maxId = idNum;
                }
            } catch (Exception e) {
                // Bỏ qua các ID không đúng định dạng
            }
        }
        // Trả về ID mới, tăng lên 1 (ví dụ: maxId là 5 -> trả về "I006")
        return "I" + String.format("%03d", maxId + 1);
    }

    /**
 * Tìm một nguyên liệu cụ thể bằng ID duy nhất của nó.
 *
 * @param ingredientId Mã ID của nguyên liệu (ví dụ: "I001")
 * @return Đối tượng Ingredient, hoặc null nếu không tìm thấy.
 */
public Ingredient findById(String ingredientId) {
    return this.ingredientsCache.stream()
            .filter(ingredient -> Objects.equals(ingredient.getIngredientId(), ingredientId))
            .findFirst() // Tìm phần tử đầu tiên khớp
            .orElse(null); // Trả về null nếu không tìm thấy
}

/**
 * Xóa một nguyên liệu cụ thể (theo ID của nó) khỏi cache và lưu file.
 *
 * @param ingredientId Mã ID của nguyên liệu cần xóa.
 * @return true nếu xóa thành công, false nếu không tìm thấy.
 */
public boolean delete(String ingredientId) {
    // removeIf trả về true nếu có phần tử bị xóa
    boolean changed = this.ingredientsCache.removeIf(
            ingredient -> Objects.equals(ingredient.getIngredientId(), ingredientId)
    );

    if (changed) {
        saveToFile(); // Chỉ lưu file nếu có thay đổi
    }
    return changed;
}   
}