package com.hospital.finance;

import java.time.Instant;
import java.util.Objects;

/** Immutable posting, not a mutable account balance. No persistence side effects. */
public final class LedgerEntry {
    private final String id;
    private final String operationId;
    private final LedgerBook book;
    private final LedgerDirection direction;
    private final Money amount;
    private final Integer patientId;
    private final String actorId;
    private final Instant occurredAt;
    private final String reversesEntryId;

    private LedgerEntry(String id, String operationId, LedgerBook book,
                        LedgerDirection direction, Money amount, Integer patientId,
                        String actorId, Instant occurredAt, String reversesEntryId) {
        this.id = required(id, "id");
        this.operationId = required(operationId, "operationId");
        this.book = Objects.requireNonNull(book, "book");
        this.direction = Objects.requireNonNull(direction, "direction");
        this.amount = Objects.requireNonNull(amount, "amount");
        if (amount.isZero()) throw new IllegalArgumentException("Posting amount must be positive");
        if (patientId != null && patientId <= 0) throw new IllegalArgumentException("Invalid patientId");
        if (book == LedgerBook.PATIENT_BALANCE && patientId == null)
            throw new IllegalArgumentException("Balance posting requires patientId");
        this.patientId = patientId;
        this.actorId = required(actorId, "actorId");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.reversesEntryId = reversesEntryId;
    }

    public static LedgerEntry posting(String id, String operationId, LedgerBook book,
                                      LedgerDirection direction, Money amount, Integer patientId,
                                      String actorId, Instant occurredAt) {
        return new LedgerEntry(id, operationId, book, direction, amount, patientId, actorId, occurredAt, null);
    }

    /** A linked opposite posting. Caller must atomically check cumulative reversal limits.
     * This does not choose whether a business refund goes to cash or patient balance.
     */
    public LedgerEntry reverse(String id, String operationId, Money amount, String actorId, Instant at) {
        if (reversesEntryId != null) throw new IllegalArgumentException("Reverse the original entry, not a reversal");
        if (this.id.equals(id)) throw new IllegalArgumentException("Reversal requires a new entry id");
        if (amount.compareTo(this.amount) > 0) throw new IllegalArgumentException("Reversal exceeds original amount");
        return new LedgerEntry(id, operationId, book, direction.opposite(), amount, patientId, actorId, at, this.id);
    }

    private static String required(String value, String name) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(name + " is required");
        return value;
    }
    public String getId() { return id; }
    public String getOperationId() { return operationId; }
    public LedgerBook getBook() { return book; }
    public LedgerDirection getDirection() { return direction; }
    public Money getAmount() { return amount; }
    public Integer getPatientId() { return patientId; }
    public String getActorId() { return actorId; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getReversesEntryId() { return reversesEntryId; }
}
