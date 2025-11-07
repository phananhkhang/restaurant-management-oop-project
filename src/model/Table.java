package model;

import model.common.CsvEntity;
import model.enums.TableStatus;

// Bàn trong nhà hàng/quán dùng enum TableStatus
public class Table implements CsvEntity {
    private String tableId;      // Mã bàn
    private String tableName;    // Tên bàn
    private TableStatus status;  // Trạng thái
    
    public Table() {
        this.status = TableStatus.AVAILABLE;
    }

    public Table(String tableId, String tableName, TableStatus status) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.status = status == null ? TableStatus.AVAILABLE : status;
    }

    // Getter/Setter
    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }

    // Tiện ích thay đổi trạng thái
    public boolean isAvailable() { return status == TableStatus.AVAILABLE; }
    
    public void occupy() { 
        if (this.status != TableStatus.AVAILABLE) {
            throw new IllegalStateException("Không thể chiếm bàn! Bàn đang ở trạng thái: " + this.status);
        }
        this.status = TableStatus.OCCUPIED; 
    }
    
    public void release() { 
        if (this.status != TableStatus.OCCUPIED) {
            throw new IllegalStateException("Không thể trả bàn! Bàn đang ở trạng thái: " + this.status);
        }
        this.status = TableStatus.AVAILABLE; 
    }

    public void displayInfo() {
        System.out.printf("%-6s %-16s %-10s%n",
                tableId, tableName, status.name());
    }


    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
            "tableId", "tableName", "status"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(tableId),
            s(tableName),
            s(status)
        };
    }
    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}