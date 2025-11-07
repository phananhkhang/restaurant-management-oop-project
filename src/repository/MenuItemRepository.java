package repository;

import model.MenuItem;
import model.common.CsvEntity;
import model.enums.MenuType;
import static repository.RepositoryUtils.*;

public class MenuItemRepository extends AbstractCsvRepository<MenuItem> {
    public MenuItemRepository(String filePath) {
        super(
            filePath,
            new MenuItem().csvHeader(),
            MenuItem::getItemId,
            MenuItemRepository::parse
        );
    }

    private static MenuItem parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 7) throw new IllegalArgumentException("Bad CSV: " + line);
        
        return new MenuItem(
            p[0], p[1],
            parseEnum(MenuType.class, p[2], MenuType.FOOD),
            parseDouble(p[3]),
            p[4],
            parseBoolean(p[5]),
            parseInt(p[6])
        );
    }
}

