package com.hospital.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Non-negative two-decimal monetary magnitude. Direction belongs to the ledger entry.
 * Currency conversion and commission rounding are deliberately outside this type.
 */
public final class Money implements Comparable<Money> {
    public static final Money ZERO = Money.of("0.00");
    private final BigDecimal value;

    private Money(BigDecimal value) {
        Objects.requireNonNull(value, "value");
        if (value.signum() < 0) throw new IllegalArgumentException("Amount must not be negative");
        this.value = value.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static Money of(String value) { return new Money(new BigDecimal(value)); }
    public static Money of(BigDecimal value) { return new Money(value); }
    public BigDecimal asBigDecimal() { return value; }
    public boolean isZero() { return value.signum() == 0; }
    public Money plus(Money other) { return new Money(value.add(other.value)); }
    public Money minus(Money other) { return new Money(value.subtract(other.value)); }
    @Override public int compareTo(Money other) { return value.compareTo(other.value); }
    @Override public boolean equals(Object other) {
        return this == other || other instanceof Money && value.equals(((Money) other).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value.toPlainString(); }
}
