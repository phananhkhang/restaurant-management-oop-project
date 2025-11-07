package repository;

import model.common.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tiện ích chung cho các Repository - tránh duplicate code
 */
public class RepositoryUtils {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Parse các kiểu dữ liệu cơ bản
    public static int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static boolean parseBoolean(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        return Boolean.parseBoolean(s.trim());
    }

    public static LocalDateTime parseDateTime(String s) {
        if (s == null || s.trim().isEmpty()) return LocalDateTime.now();
        try {
            return LocalDateTime.parse(s.trim(), ISO);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    // Parse Money từ format "100.50 VND" hoặc "100.50"
    public static Money parseMoney(String s) {
    if (s == null || s.trim().isEmpty()) return new Money(BigDecimal.ZERO);
    try {
        String[] parts = s.trim().split("\\s+");
        BigDecimal amount = new BigDecimal(parts[0]);
        // Bỏ qua currency vì class Money không có currency nữa
        return new Money(amount);
    } catch (Exception e) {
        return new Money(BigDecimal.ZERO);
    }
    }


    // Parse enum an toàn với giá trị mặc định
    public static <E extends Enum<E>> E parseEnum(Class<E> enumType, String value, E defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Enum.valueOf(enumType, value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // Private constructor để không cho khởi tạo
    private RepositoryUtils() {}
}

