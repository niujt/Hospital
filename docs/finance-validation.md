# Finance validation — 2026-10-31

## Scope
Local `feature/local-finance` implementation only. No push, deployment or real database changes.
All executed mutations used isolated in-memory H2 and synthetic fixtures, not real patients,
real money, real bank cards or external payment APIs.

## Automated verification
- Offline Maven + JDK17: **311 tests, 0 failures, 0 errors**. Existing controller/template tests
  included alongside finance amount, auth, CSRF, payment-security, card, HTTP-boundary,
  legacy-inventory and transactional tests.
- JavaScript syntax check passed for the integration UI at validation time. UI-specific tests
  and visual acceptance are maintained separately by the frontend owner.
- `git diff --check` passed.

The finance transaction tests use actual Spring transaction proxies, JDBC and H2 2.1.214
(MySQL compatibility mode), not a mocked database. Payment-security cryptography tests are
separate; transaction tests mock password verification to isolate ledger behavior.

Covered transaction cases include:
1. Pending recharge never credits until confirmation.
2. Concurrent different-key confirmation credits once.
3. Same-key request retry returns original; altered payload rejected.
4. Wallet sale does not duplicate cash income.
5. Concurrent different-key payment of one order deducts once.
6. Concurrent same-key order creation creates one order; lost-response replay returns same sale.
7. Insufficient balance and competing orders cannot overdraw/oversell.
8. Injected failure after earlier writes rolls back recharge/cash/balance, sale/stock, procurement,
   and refund totals; no partial accounting state remains.
9. Concurrent refund cannot exceed original sale; replay does not double-credit.
10. Commission uses original sale snapshot after policy changes; partial/final refunds reverse
    the original total exactly. Unattributed sale commission is zero.
11. Actual completed encounter is unique per appointment; foreign doctor and future date rejected.
12. Patient DTOs exclude commission; patient routes ignore spoofed patient/doctor query IDs.
13. Doctor may create wallet order but cannot collect/pay with patient password; patient pays it.
14. Cash-sale refund returns balance, not cash outflow; inventory is not silently restocked.
15. Ledger reports exclude duplicate recharge view, use actual cash events and Shanghai Monday weeks.
16. Foreign keys protect financially referenced drug/patient records; legacy metadata SQL never
    updates stock; new drug cannot import initial stock through old CRUD.

## Actual Spring Boot HTTP smoke
Started the full app on isolated port **8090** with reviewed synthetic schema and explicit
`hospital.finance.initialize-schema=true`. Startup succeeded; legacy-schema initialization
completed before financial foreign keys were created. Test process was stopped afterward.

**37 successful HTTP checks**, plus deliberate denied actions and concurrent double-payment
verification, exercised real controllers, interceptors, Spring transactions and JDBC:
- All three roles login and load their finance page; spoofed request role/id does not authenticate
  as another role; CSRF handshake used.
- Set independent system password; create masked DEMO card; request BANK recharge.
- Administrator confirms twice; wallet remains credited once.
- Assigned doctor marks encounter complete, sets up linked pending order; doctor payment denied.
- Patient pays the same order concurrently with different keys; both responses reference one sale.
- Administrator refunds to balance and records procurement; cash and doctor report APIs return data.
- Patient cannot access cash ledger or drug policies; patient sale DTO contains no commission.

Temporary local evidence (not source artifacts): `/tmp/hospital-finance-all.log`,
`/tmp/hospital-finance-http-smoke-app.log`, `/tmp/hospital-finance-http-smoke-result.json`.
The temporary HTTP script reads only runtime synthetic login fixtures; credentials and runtime
configuration are deliberately NOT committed. Port8090 was separate from baseline screenshots.

## Limits — not a production-readiness assertion
- **Real MySQL was not executed**: no mysqld/mariadbd/docker was available. H2 success does not
  prove production MySQL locking, isolation, deadlocks, restart recovery or deployment behavior.
- Real payment/bank networks are intentionally not implemented or tested.
- Browser screenshots, responsive layout and every user interaction are a separate integration
  pass. Passing API/unit tests is not a claim that all frontend screenshots are complete.
- Production still needs isolated MySQL concurrency/restart testing, backup/restore verification,
  rate limiting/recovery policy, operational monitoring and full legacy clinical data-scope review.

## Frontend checks (separate from actual browser acceptance)
- Native Layui 2.13.9 admin/doctor pages; patient page uses existing portal and patient-only dialog skin.
- Apache ECharts 6.1.0 official npm tarball integrity SHA512 verified; unmodified dist/license/notice vendored.
- `node --check src/main/resources/static/js/finance.js` passed.
- `NODE_PATH=<test-only jsdom installation>/node_modules node tools/check-finance-ui.cjs` passed:
  three role rendering, escaped untrusted catalog labels, patient never requests staff policy,
  UUID card/encounter identifiers, CSRF and idempotency headers, password clearing, data-derived
  wallet chart, lost-response retry reusing intent across page-internal refresh.
- `node tools/check-feedback.cjs` passed for inherited role-aware feedback and pending guards.
- These DOM/API-mocked checks are **not** real browser screenshots or touch testing. Financial UI
  actual-browser acceptance was subsequently performed on the isolated 8089 application, as
  described below; this is separate from baseline 8088.
- Financial forms never persist passwords/card details in localStorage/sessionStorage. Page-memory
  idempotency keys survive the page's refresh-data button, not a full navigation/browser restart;
  after an ambiguous result, inspect existing records before creating another intent.

## Incremental existing-form/account integration
- Existing drug form API now atomically saves catalog metadata plus commission policy; stock0
  on create and untouched on edit. Tests cover invalid rate/type/price, duplicate-name contention,
  policy-write failure rolling back metadata, and patient/staff permission separation.
- Real HTTP smoke creates/edits an atomic drug record and confirms a submitted number cannot
  override stock. A separate admin-created doctor profile is assigned an account, then its new
  salted-hash credential successfully logs in as doctor despite spoofed role/id inputs.
- Public doctor registration attempts are rejected; public doctor JSON is limited to five fields.
- Admin doctor edit never returns stored password/hash; blank password preserves existing hash.
- Real HTTP patient appointment with forged patientid is verified to belong to the authenticated
  patient's own profile afterward. The old max-id appointment linking is not redesigned here.
- Public patient registration still requires a pre-recorded unbound patient profile. Username
  uniqueness across all legacy registration/admin paths is not retroactively migrated; audit and
  enforce a database unique username constraint after resolving any legacy duplicates before
  production. New doctor assignment requests serialize validation within the new API.

## Actual browser acceptance — 8089, 2026-10-31
- User-authorized synthetic admin, patient and doctor accounts all logged in and reached their own UI.
- Patient configured independent demo payment password and masked mock card; requested 200 recharge,
  saw pending with zero balance; administrator confirmed and patient balance became 200 once.
- Administrator set existing drug commission to 12.5% using the original edit form with fixed
  outer Save/Cancel footer; separate procurement of five units at 6 produced expense30 and stock105.
- Doctor completed one appointment and created a two-unit order at10. Patient canceled confirmation
  with Escape: no payment; wrong password stayed in dialog with error and no deduction; correct
  password paid20, balance180, stock103, one paid order.
- Administrator's attempted refund25 was rejected. Refund5 succeeded to balance, patient saw185.
  Commission2.50 was reversed0.63, leaving1.87. Stock remained103; refund does not imply return.
- Actual ECharts rendered patient in200/out20 and admin cash in200/out30/net170. After wallet
  purchase/refund, cash totals stayed unchanged. Doctor day/week/month showed one encounter and
  net commission1.87; Monday week label2026-10-26 and month2026-10 were checked.
- Two real integration defects fixed with regression coverage: absent optional patientId admin
  filter unboxing; detached Layer content rendering an invisible finance confirmation dialog.
- Patient compact dark success HUD with green check was captured after actual synthetic PDF output.
- These are selected critical flows, not a claim every legacy screen, responsive size, print
  pagination, or animation frame was manually tested. Receipt browser/print acceptance remains
  pending at this checkpoint. Real MySQL verification remains unavailable as stated above.

## Final integrated checkpoint — 2026-10-31 12:43 UTC
- Full offline Maven verify: 339 tests, zero failures/errors/skips, BUILD SUCCESS. Native workspace,
  modal, finance, receipt, drug form and feedback contract checks pass separately.
- Real 8089 app now contains native Layui admin multi-tab workspace and same-appointment doctor
  diagnosis/history/order tabs. Actual browser checks: tab deduplication, draft refresh warning,
  canceled warning preserving draft, explicit discard/close, read-only history with no Save,
  and current patient/appointment order binding all passed. A transient about:blank iframe load
  incorrectly flashing session-expired was fixed without widening route permissions.
- Receipt JSON and genuine authenticated PDF were verified on the live synthetic app: 3% inclusive
  tax, paid gross20, net19.42, tax0.58; refund5 includes tax0.15, remaining gross15. Repeated reads
  do not mutate ledgers; doctor PDF access403. Actual downloaded PDF rendered cleanly in Chinese.
  Multi-page PDF test covers150 rows, repeated header and per-page demonstration notice.
- The patient receipt page was reached from its actual purchase link and displayed the same
  values, masked patient name, original sale identity and explicit non-reimbursable demo notice.
- Patient HUD is now soft light-blue with no hard border, replacing the earlier dark checkpoint.
- Browser native print dialog did not open in this cloud browser; authenticated downloadable PDF
  is functional. No claim of native printer, physical print, production MySQL or full mobile QA.
- Historical route audit remains separate:38 normal rendered routes and12 runtime-only fictional
  privacy placeholders. This does not imply every legacy CRUD/interaction or current route was
  fully regressed. Remaining feasible legacy tests are being recorded in the coverage addendum.

## Administrator credential hardening — 2026-10-31 12:51 UTC
- User explicitly approved removing old password display and safe hashing. Full offline verify now
  passes359 tests, no failures/errors/skips (`/tmp/hospital-hash-full-verify.log`).
- Administrator create/change uses the same salted PBKDF2 codec as newly assigned doctor accounts;
  new passwords remain8–72 characters. Editing with a blank password preserves it. Admin list/detail
  model copies exclude both plaintext and hash; Login.toString excludes the password.
- Correct legacy login upgrades storage using a byte-sensitive compare-and-set, without changing
  role. Wrong passwords do not migrate; a competing password reset is not overwritten, and a lost
  CAS revalidates the current record. Existing hashes are not rehashed. The short existing demo
  password can migrate without weakening the new-password policy.
- Additional true H2/MyBatis/MockMvc legacy tests cover administrator CRUD/duplicate/role boundaries,
  hospitalization persistence, and XLS export content type/header/column correctness. Required
  hospitalization business validation remains a known legacy gap, not claimed repaired.
- These final Java changes are built but need the coordinated8089 restart before runtime claims.
  MySQL's actual transaction/collation behavior remains unverified; H2 is not a production guarantee.
