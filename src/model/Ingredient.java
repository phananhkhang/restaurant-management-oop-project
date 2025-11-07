package model;
import model.common.CsvEntity;
import model.enums.Unit;

public class Ingredient implements CsvEntity {
    private String ingredientId;  // Định nghĩa mã nguyên liệu
    private String name;           // Định nghĩa tên nguyên liệu
    private Unit unit;          // Định nghĩa đơn vị
    private double quantity;      // Định nghĩa số lượng
    private String itemId; // Định nghĩa mã Mon An 
    public Ingredient() {}

    public Ingredient(String ingredientId, String name, Unit unit, double quantity, String itemId) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.unit = unit;
        this.setQuantity(quantity);
        this.itemId = itemId;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = Math.max(0, quantity);
    }

    //Csv methods

    @Override
    public String [] csvHeader() {
        return new String[] {"ingredientId", "itemId", "name", "unit", "quantity"};
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            ingredientId,
            itemId,
            name,
            unit.getDisplayValue() ,// Sử dụng phương thức getDisplayValue() để lấy giá trị hiển thị của Enum
            String.valueOf(quantity)
        };
    }
}