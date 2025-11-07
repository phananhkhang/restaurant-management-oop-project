package model.common;

import java.math.BigDecimal;

public class Money {
    private BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount == null ? BigDecimal.ZERO : amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    @Override
    public String toString() {
        return amount + " VND";
    }
}
