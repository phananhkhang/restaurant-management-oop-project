package repository;

import model.StaffShift;
import model.common.CsvEntity;
import model.enums.StaffShiftStatus;
import static repository.RepositoryUtils.*;

public class StaffShiftRepository extends AbstractCsvRepository<StaffShift> {
    public StaffShiftRepository(String filePath) {
        super(
            filePath,
            new StaffShift().csvHeader(),
            StaffShift::getStaffShiftId,
            StaffShiftRepository::parse
        );
    }

    private static StaffShift parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 5) throw new IllegalArgumentException("Bad CSV: " + line);
        
        // Thứ tự CSV: staffShiftId, staffId, shiftId, status, workDate, notes
        return new StaffShift(
            p[0],  // staffShiftId
            p[1],  // staffId
            p[2],  // shiftId
            parseEnum(StaffShiftStatus.class, p[3], StaffShiftStatus.SCHEDULED), // status
            p[4]   // workDate
        );
    }
}

