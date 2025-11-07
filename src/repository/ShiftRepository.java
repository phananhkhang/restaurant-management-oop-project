package repository;

import model.Shift;
import model.common.CsvEntity;
import model.enums.ShiftType;
import static repository.RepositoryUtils.*;

public class ShiftRepository extends AbstractCsvRepository<Shift> {
    public ShiftRepository(String filePath) {
        super(
            filePath,
            new Shift().csvHeader(),
            Shift::getShiftId,
            ShiftRepository::parse
        );
    }

    private static Shift parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 6) throw new IllegalArgumentException("Bad CSV: " + line);
        
        Shift shift = new Shift(
            p[0], p[1], p[2], p[3],
            parseEnum(ShiftType.class, p[4], ShiftType.MORNING)
        );
        
        // Parse description (cột thứ 6)
        if (p.length >= 6 && !p[5].isEmpty()) {
            shift.setDescription(p[5]);
        }
        
        return shift;
    }
}

