package model;
import model.common.CsvEntity;
import model.enums.StaffShiftStatus;

// Phân ca cho nhân viên dùng enum StaffShiftStatus
public class StaffShift implements CsvEntity {
    private String staffShiftId;     // Mã phân ca
    private String staffId;          // Mã nhân viên
    private String shiftId;          // Mã ca
    private StaffShiftStatus status; // Trạng thái 
    private String workDate;       // Ngày làm việc
    public StaffShift() {
        this.status = StaffShiftStatus.SCHEDULED;
    }

    public StaffShift(String staffShiftId, String staffId, String shiftId,
                      StaffShiftStatus status, String workDate) {
        this.staffShiftId = staffShiftId;
        this.staffId = staffId;
        this.shiftId = shiftId;
        this.status = status == null ? StaffShiftStatus.SCHEDULED : status;
        this.workDate = workDate;
    }

    // Getter/Setter
    public String getStaffShiftId() { return staffShiftId; }
    public void setStaffShiftId(String staffShiftId) { this.staffShiftId = staffShiftId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getShiftId() { return shiftId; }
    public void setShiftId(String shiftId) { this.shiftId = shiftId; }

    public StaffShiftStatus getStatus() { return status; }
    public void setStatus(StaffShiftStatus status) { this.status = status; }
    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }
    public void displayInfo() {
        System.out.printf("%-12s %-8s %-8s %-10s %-10s%n",
                staffShiftId, staffId, shiftId, status.name(), workDate);
    }
    // CsvEntity methods
    @Override
    public String[] csvHeader() {
        return new String[] {
            "staffShiftId", "staffId", "shiftId", "status", "workDate"
        };
    }
    @Override
    public String[] toCsvRow() {
        return new String[] {
            s(staffShiftId),
            s(staffId),
            s(shiftId),
            s(status),
            s(workDate)
        };
    }
    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}
