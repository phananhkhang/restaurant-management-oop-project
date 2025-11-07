package repository;

import model.Table;
import model.common.CsvEntity;
import model.enums.TableStatus;
import static repository.RepositoryUtils.*;

public class TableRepository extends AbstractCsvRepository<Table> {
    public TableRepository(String filePath) {
        super(
            filePath,
            new Table().csvHeader(),
            Table::getTableId,
            TableRepository::parse
        );
    }
    private static Table parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        // CSV format: tableId,tableName,status (3 columns)
        if (p.length < 3) throw new IllegalArgumentException("Bad CSV: " + line);
        
        Table table = new Table(
            p[0],                                                          // tableId
            p[1],                                                          // tableName
            parseEnum(TableStatus.class, p[2], TableStatus.AVAILABLE)     // status
        );
        return table;
    }
}

