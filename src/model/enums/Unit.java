package model.enums;

public enum Unit {
    // Định nghĩa các hằng số
    GRAM("g"),
    KILOGRAM("kg"),
    MILLILITER("ml"),
    LITER("l"),
    CAI("cái"), // (Item/Piece)
    MUONG("muỗng"), // (Spoon)
    TRAI("trái"); // (Fruit/Item)

    // Thuộc tính để lưu giá trị hiển thị/lưu trữ (ví dụ: "g" thay vì "GRAM")
    private final String displayValue;

    // Constructor cho enum
    Unit(String displayValue) {
        this.displayValue = displayValue;
    }

    // Getter để lấy giá trị hiển thị
    public String getDisplayValue() {
        return displayValue;
    }

    // 💡 Phương thức static tiện ích:
    // Dùng để chuyển đổi String từ file CSV thành Enum khi đọc dữ liệu
    public static Unit fromDisplayValue(String displayValue) {
        for (Unit u : Unit.values()) {
            if (u.displayValue.equalsIgnoreCase(displayValue)) {
                return u;
            }
        }
        // Hoặc ném ra một Exception nếu không tìm thấy
        throw new IllegalArgumentException("Không tìm thấy đơn vị hợp lệ cho: " + displayValue);
    }
}