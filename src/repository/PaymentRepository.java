package repository;

import model.Payment;
import model.common.CsvEntity;
import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import static repository.RepositoryUtils.*;

public class PaymentRepository extends AbstractCsvRepository<Payment> {
    public PaymentRepository(String filePath) {
        super(
            filePath,
            new Payment().csvHeader(),
            Payment::getPaymentId,
            PaymentRepository::parse
        );
    }

    private static Payment parse(String line) {
        String[] p = CsvEntity.parseLine(line);
        if (p.length < 9) throw new IllegalArgumentException("Bad CSV: " + line);
        
        Payment x = new Payment();
        x.setPaymentId(p[0]);
        x.setOrderId(p[1]);
        x.setPaymentMethod(parseEnum(PaymentMethod.class, p[2], PaymentMethod.CASH));
        x.setAmount(parseDouble(p[3]));
        x.setPaymentDate(p[4]);
        x.setPaymentTime(p[5]);
        x.setStatus(parseEnum(PaymentStatus.class, p[6], PaymentStatus.PENDING));
        x.setTransactionId(p[7]);
        x.setNotes(p[8]);
        return x;
    }
}