package model;

import model.common.CsvEntity;
import model.enums.ShiftType;

// Ca làm việc dùng enum ShiftType
public class Shift  implements CsvEntity{
    private String shiftId;      // Mã ca
    private String shiftName;    // Tên ca
    private String startTime;    // HH:mm
    private String endTime;      // HH:mm
    private ShiftType shiftType; // Loại ca
    private String description;  // Mô tả ca làm việc

    public Shift() {}

    public Shift(String shiftId, String shiftName, String startTime, String endTime,
                 ShiftType shiftType) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftType = shiftType;
        this.description = "";
    }

    // Getter/Setter
    public String getShiftId() { return shiftId; }
    public void setShiftId(String shiftId) { this.shiftId = shiftId; }

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftType shiftType) { this.shiftType = shiftType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void displayInfo() {
        System.out.printf("%-8s %-16s %-6s %-6s %-10s%n",
                shiftId, shiftName, startTime, endTime, shiftType.name());
    }

    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
            "shiftId", "shiftName", "startTime", "endTime",
            "shiftType", "description"
        };
    }

    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(shiftId),
            s(shiftName),
            s(startTime),
            s(endTime),
            s(shiftType),
            s(description)
        };
    }

    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}