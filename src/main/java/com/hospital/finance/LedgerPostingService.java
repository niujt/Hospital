package com.hospital.finance;

import java.util.List;

/** Port only, intentionally no Spring bean or database implementation.
 * Implementations must authorize the actor and patient scope; validate operation semantics;
 * atomically write every posting plus the durable idempotency result; serialize balance debits
 * and refund-cap checks. Same key and same payload returns the existing operation id, while
 * the same key with a different payload is rejected. A failed transaction leaves no postings.
 * The caller-supplied book/direction alone is never sufficient authorization.
 */
public interface LedgerPostingService {
    String postAtomically(String idempotencyKey, List<LedgerEntry> entries);
}
