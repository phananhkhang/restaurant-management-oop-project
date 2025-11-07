package repository;

import model.InventoryItem;
import model.common.CsvEntity;
import model.enums.InventoryStatus;
import model.enums.Unit;
import static repository.RepositoryUtils.*;

public class InventoryItemRepository extends AbstractCsvRepository<InventoryItem> {
    public InventoryItemRepository(String filePath) {
        super(
            filePath,
            new InventoryItem().csvHeader(),
            InventoryItem::getStockItemId,
            InventoryItemRepository::parse
        );
    }

    private static InventoryItem parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 5) throw new IllegalArgumentException("Bad CSV: " + line);
        
        return new InventoryItem(
            p[0], p[1],
            parseEnum(Unit.class, p[2], Unit.CAI),
            parseDouble(p[3]),
            parseEnum(InventoryStatus.class, p[4], InventoryStatus.IN_STOCK)
        );
    }
}

