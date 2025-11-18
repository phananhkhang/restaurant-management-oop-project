package repository;

import model.Bill;
import model.common.CsvEntity;
import model.enums.BillStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static repository.RepositoryUtils.*;

public class BillRepository extends AbstractCsvRepository<Bill> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public BillRepository(String filePath) {
        super(
            filePath,
            new Bill().csvHeader(),
            Bill::getId,
            BillRepository::parse
        );
    }

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            // Thử parse format mới (yyyy-MM-dd HH:mm:ss)
            return LocalDateTime.parse(s.trim(), FORMATTER);
        } catch (Exception e1) {
            try {
                // Fallback: parse format cũ (ISO_LOCAL_DATE_TIME với nanoseconds)
                return LocalDateTime.parse(s.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private static Bill parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        // CSV format: id,createdAt,updatedAt,closedAt,tableId,status,note,subTotal,discountAmount,totalAmount,paidAmount,items (12 columns)
        if (p.length < 12) throw new IllegalArgumentException("Bad CSV: " + line);
        
        Bill bill = new Bill();
        
        // Đọc ID từ CSV thay vì tự sinh
        if (!p[0].isEmpty()) {
            bill.setId(p[0]);
        }
        
        // Đọc timestamps
        bill.setCreatedAt(parseDateTime(p[1]));
        bill.setUpdatedAt(parseDateTime(p[2]));
        bill.setClosedAt(parseDateTime(p[3]));
        
        // Đọc tableId
        if (!p[4].isEmpty()) {
            bill.setTableId(p[4]);
        }
        
        // Đọc các trường còn lại
        bill.setStatus(parseEnum(BillStatus.class, p[5], BillStatus.PENDING));
        bill.setNote(p[6]);
        bill.setSubTotal(parseMoney(p[7]));
        bill.setDiscountAmount(parseMoney(p[8]));
        bill.setTotalAmount(parseMoney(p[9]));
        bill.setPaidAmount(parseMoney(p[10]));
        
        // Đọc items (cột 11) - phân tách bằng dấu |
        if (!p[11].isEmpty()) {
            String[] itemsArray = p[11].split("\\|");
            for (String item : itemsArray) {
                if (!item.trim().isEmpty()) {
                    bill.addItem(item.trim());
                }
            }
        }
        return bill;
    }
}
