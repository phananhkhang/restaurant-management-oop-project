package model.common;
/**
 * CsvEntity: Giao diện đánh dấu và tuần tự hóa đối tượng sang CSV.
 * - csvHeader(): tên các cột (thứ tự cố định).
 * - toCsvRow(): giá trị các cột tương ứng (thứ tự giống csvHeader).
 * - toCsvLine(): tiện ích tạo 1 dòng CSV hoàn chỉnh từ toCsvRow().
 * - parsesLine(): chuyển dòng csv thành 1 mảng String **/

public interface CsvEntity {
    String[] csvHeader();
    String[] toCsvRow();

    default String toCsvLine() {
        return String.join(",", toCsvRow());
    }

    static String toHeaderLine(CsvEntity entity) {
        return String.join(",", entity.csvHeader());
    }

    static String[] parseLine(String line) {
        if (line == null) return new String[]{""};
        if (line.startsWith("\ufeff")) {
            line = line.substring(1);
        }
        return line.split(",", -1);
    }
}
