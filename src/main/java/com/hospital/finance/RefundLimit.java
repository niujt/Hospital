package com.hospital.finance;

import java.util.Objects;

/** Arithmetic guard only: persistence must lock/recheck the original operation atomically. */
public final class RefundLimit {
    private RefundLimit() { }
    public static Money remaining(Money original, Money alreadyRefunded) {
        return Objects.requireNonNull(original, "original").minus(
                Objects.requireNonNull(alreadyRefunded, "alreadyRefunded"));
    }
    public static void requireAvailable(Money original, Money alreadyRefunded, Money requested) {
        Objects.requireNonNull(requested, "requested");
        if (requested.isZero()) throw new IllegalArgumentException("Refund must be positive");
        if (requested.compareTo(remaining(original, alreadyRefunded)) > 0)
            throw new IllegalArgumentException("Refund exceeds remaining amount");
    }
}
