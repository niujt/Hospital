# Finance design — isolated local implementation

The initial pure-Java foundation has now been extended with real transactional JDBC services,
role-checked API endpoints, optional schema initialization, independent system payment password,
simulated cards, and isolated tests. API contract: [finance-api-contract.md](finance-api-contract.md).
Nothing here connects to real WeChat, Alipay or banking services. No production DB was applied.

## Confirmed business policy
- Patient requests simulated recharge; administrator confirms once, producing cash IN and
  balance IN as two views of the SAME event. Pending request is not credited.
- Patient pays a pending wallet order using their own independent SYSTEM payment password.
  Doctors only create wallet orders; administrator may record CASH sale receipt.
- Wallet purchase is patient balance OUT, not another cash receipt.
- Refund is admin-controlled, always balance IN, never cash OUT. It is a money-only refund:
  physical return of stock is NOT assumed. Original sale remains, refund is a linked new record.
- Each drug's percentage is admin-editable and staff-readable, not in patient DTOs. Default0;
  snapshot at payment; unattributed sale0; refund reverses the original commission snapshot.
- Actual doctor completion is an explicit action on their assigned appointment, unique per
  appointment. No pageview counts, no marking future appointments complete.
- Daily/weekly/monthly buckets use Asia/Shanghai; Monday begins week. Monetary currency model
  is CNY with two-decimal HALF_UP commission calculations; cumulative prorata partial refunds
  use the original snapshot, final refund removes remaining commission rounding residue.

## Storage and transactions
`src/main/resources/db/finance/V1__finance.sql` is opt-in via
`hospital.finance.initialize-schema=true`, intended for reviewed migrations or isolated demos.
It creates separate operations, recharge requests, wallet projections, immutable ledger entries,
orders, sales, refunds, procurement records, encounters, staff-only drug policies, payment-security
hashes and demo-card metadata. Existing price fields are NOT imported as historical paid sales.

`@Transactional` service methods write idempotency result, money movements, inventory, snapshot
commission and state transitions together. Row locks serialize confirmation, order payment,
stock changes, wallet debits and refund totals. A unique actor/request key rejects changed payloads
and returns prior result on identical retries. Order PAID state separately prevents duplicate
payment even with different keys. Application servers must reuse the same key for retrying one
intent; a genuinely new order/refund uses a new key. If a database operation fails, mutations roll
back. No endpoint permits editing/deleting ledger entries.

Live MySQL transaction semantics still require an isolated MySQL integration run before production:
H2 MySQL-mode tests are NOT that validation. Ensure legacy drugs/patient/doctor/appointment tables
use InnoDB and have compatible IDs before applying foreign keys; back up and review first. Startup
with initialization disabled must have the reviewed schema already installed. Do not set it on
unknown databases or point this demo at real patient/financial data.

## Authorization and legacy compatibility
Login identity is established only after password verification; request role/id discarded,
failed authentication clears session, session ID rotates. FinanceAccess reloads role from DB.
Finance mutations require CSRF token; supplied cross-origin Origin is rejected. /admin routes
are now admin-only; clinical doctor/patient routes get trusted-role gates. Legacy clinical
patient-assignment enforcement is not fully redesigned and remains a separate hardening task.

Old PUT /doctor/drug is blocked because it directly changed inventory without sale accounting.
Legacy drug metadata update no longer writes number; added drugs start with zero stock; stock
increases use procurement. Financial references are protected with foreign keys. Deleting a
referenced record is intentionally not equivalent to removing financial history.

System payment password uses PBKDF2-HMAC-SHA256, random salt, 210000 iterations; never plaintext
or response hash. Initial setup verifies login password, changes verify old system password.
Cards accept only DEMO issuer and four-digit suffix, not full PAN/CVV or real bank passwords.
No password is included in a generated DTO toString, fingerprint, ledger, report or exception.

## Tests and remaining operational gates
Tests include amount precision; own-patient/role boundaries; authentication forgery rejection;
CSRF; password hashing and masked cards; same-key retries; concurrent different-key confirmation
and payment; competing balance/stock; injected failures after money/stock writes; refund caps;
original commission snapshots; no duplicate cash income; actual-completion counting; safe DTOs.

Before production: real MySQL concurrency/deadlock/restart tests, backup/restore drill, monitoring,
password attempt rate limiting/account recovery policy, deployment secret management, staff
workflow acceptance and data retention policy. No completion percentage or production-readiness
claim follows from local tests. ECharts must use actual authorized aggregates, including empty
series, never fabricated values.
