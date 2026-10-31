package com.hospital.finance;

public enum LedgerDirection {
    IN, OUT;
    public LedgerDirection opposite() { return this == IN ? OUT : IN; }
}
