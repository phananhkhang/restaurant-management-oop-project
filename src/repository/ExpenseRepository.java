package repository;

import model.Expense;
import model.common.CsvEntity;
import model.enums.ExpenseStatus;
import model.enums.ExpenseType;
import static repository.RepositoryUtils.*;

public class ExpenseRepository extends AbstractCsvRepository<Expense> {
    public ExpenseRepository(String filePath) {
        super(
            filePath,
            new Expense().csvHeader(),
            Expense::getExpenseId,
            ExpenseRepository::parse
        );
    }

    private static Expense parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 9) throw new IllegalArgumentException("Bad CSV: " + line);
        
        Expense expense = new Expense();
        expense.setExpenseId(p[0]);
        expense.setExpenseType(parseEnum(ExpenseType.class, p[1], ExpenseType.OTHER));
        expense.setAmount(parseDouble(p[2]));
        expense.setExpenseDate(p[3]);
        expense.setDescription(p[4]);
        expense.setApprovedBy(p[5]);
        expense.setStatus(parseEnum(ExpenseStatus.class, p[6], ExpenseStatus.PENDING));
        expense.setReceipt(p[7]);
        expense.setNotes(p[8]);
        return expense;
    }
}

