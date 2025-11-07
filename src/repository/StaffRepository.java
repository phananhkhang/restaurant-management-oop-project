package repository;

import model.Staff;
import model.common.CsvEntity;
import model.enums.Role;
import model.enums.StaffStatus;
import static repository.RepositoryUtils.*;

public class StaffRepository extends AbstractCsvRepository<Staff> {
    public StaffRepository(String filePath) {
        super(
            filePath,
            new Staff().csvHeader(),
            Staff::getId,
            StaffRepository::parse
        );
    }

    private static Staff parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 9) throw new IllegalArgumentException("Bad CSV: " + line);
        
        // CSV format: ID,FullName,Phone,Address,Username,Password,Role,Salary,Status
        Staff staff = new Staff(
            p[0].trim(),                                           // ID
            p[1].trim(),                                           // FullName
            p[2].trim(),                                           // Phone
            p[3].trim(),                                           // Address
            p[4].trim(),                                           // Username
            p[5].trim(),                                           // Password
            parseEnum(Role.class, p[6], Role.WAITER),             // Role
            parseDouble(p[7]),                                     // Salary
            parseEnum(StaffStatus.class, p[8], StaffStatus.ACTIVE) // Status
        );
        return staff;
    }
}

