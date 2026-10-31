package com.hospital.finance;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class FinanceFoundationTest {
    private static final Instant NOW = Instant.parse("2026-10-31T00:00:00Z");
    private LedgerEntry payment() {
        return LedgerEntry.posting("entry-1", "operation-1", LedgerBook.PATIENT_BALANCE,
                LedgerDirection.OUT, Money.of("30.00"), 42, "staff-1", NOW);
    }
    @Test void decimalAdditionIsExact() {
        assertEquals(Money.of("0.30"), Money.of("0.10").plus(Money.of("0.20")));
    }
    @Test void normalizesEquivalentAmounts() {
        assertEquals(Money.of("1"), Money.of(new BigDecimal("1.000")));
        assertEquals(Money.of("1").hashCode(), Money.of("1.00").hashCode());
        assertEquals("1.00", Money.of("1").toString());
    }
    @Test void rejectsRoundingInsteadOfSilentlyChangingMoney() {
        assertThrows(ArithmeticException.class, () -> Money.of("0.001"));
    }
    @Test void rejectsNegativeMagnitudes() {
        assertThrows(IllegalArgumentException.class, () -> Money.of("-0.01"));
    }
    @Test void subtractionCannotProduceOverdraft() {
        assertThrows(IllegalArgumentException.class, () -> Money.of("2").minus(Money.of("3")));
        assertEquals(Money.ZERO, Money.of("2").minus(Money.of("2")));
    }
    @Test void allowsZeroBalanceButNotZeroPosting() {
        assertTrue(Money.ZERO.isZero());
        assertThrows(IllegalArgumentException.class, () -> LedgerEntry.posting("e", "o",
                LedgerBook.CASH, LedgerDirection.IN, Money.ZERO, null, "staff", NOW));
    }
    @Test void balancePostingRequiresPatient() {
        assertThrows(IllegalArgumentException.class, () -> LedgerEntry.posting("e", "o",
                LedgerBook.PATIENT_BALANCE, LedgerDirection.IN, Money.of("1"), null, "staff", NOW));
    }
    @Test void cashPostingDoesNotRequirePatientForProcurement() {
        LedgerEntry entry = LedgerEntry.posting("e", "o", LedgerBook.CASH,
                LedgerDirection.OUT, Money.of("1"), null, "staff", NOW);
        assertNull(entry.getPatientId());
        assertEquals(LedgerBook.CASH, entry.getBook());
    }
    @Test void postingRequiresAttribution() {
        assertThrows(IllegalArgumentException.class, () -> LedgerEntry.posting("e", "o",
                LedgerBook.CASH, LedgerDirection.IN, Money.of("1"), null, " ", NOW));
    }
    @Test void linkedReversalPreservesOriginalAndBook() {
        LedgerEntry original = payment();
        LedgerEntry reversed = original.reverse("entry-2", "refund-1", Money.of("10"), "staff-2", NOW);
        assertEquals("entry-1", reversed.getReversesEntryId());
        assertEquals(LedgerDirection.IN, reversed.getDirection());
        assertEquals(LedgerBook.PATIENT_BALANCE, reversed.getBook());
        assertEquals(Integer.valueOf(42), reversed.getPatientId());
        assertEquals(Money.of("30"), original.getAmount());
        assertEquals(LedgerDirection.OUT, original.getDirection());
        assertNull(original.getReversesEntryId());
    }
    @Test void cannotReverseBeyondOriginal() {
        assertThrows(IllegalArgumentException.class, () -> payment().reverse("e2", "r1", Money.of("31"), "staff", NOW));
    }
    @Test void cannotOverwriteOriginalId() {
        assertThrows(IllegalArgumentException.class, () -> payment().reverse("entry-1", "r1", Money.of("1"), "staff", NOW));
    }
    @Test void reversalCannotBeReversedAsOriginal() {
        LedgerEntry reversal = payment().reverse("e2", "r1", Money.of("1"), "staff", NOW);
        assertThrows(IllegalArgumentException.class, () -> reversal.reverse("e3", "r2", Money.of("1"), "staff", NOW));
    }
    @Test void cumulativeRefundCapIncludesEarlierRefunds() {
        assertEquals(Money.of("7"), RefundLimit.remaining(Money.of("10"), Money.of("3")));
        assertDoesNotThrow(() -> RefundLimit.requireAvailable(Money.of("10"), Money.of("3"), Money.of("7")));
        assertThrows(IllegalArgumentException.class,
                () -> RefundLimit.requireAvailable(Money.of("10"), Money.of("3"), Money.of("7.01")));
    }
    @Test void cannotRefundZeroOrAlreadyExhaustedAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> RefundLimit.requireAvailable(Money.of("10"), Money.ZERO, Money.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> RefundLimit.requireAvailable(Money.of("10"), Money.of("10"), Money.of("0.01")));
    }
    @Test void inconsistentHistoricRefundTotalFailsClosed() {
        assertThrows(IllegalArgumentException.class,
                () -> RefundLimit.remaining(Money.of("10"), Money.of("11")));
    }
}
