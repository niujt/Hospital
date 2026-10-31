package com.hospital.finance;

public class FinanceException extends RuntimeException {
    private final int status;
    public FinanceException(int status, String message) { super(message); this.status = status; }
    public int getStatus() { return status; }
}
