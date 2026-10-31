# Final local rebuild validation — 2026-10-31

## Source and scope
Includes response-contract refactor, native Layui 2.13.9 administrator/doctor workspaces,
patient/public service UI and status feedback, fixed form footers, role/account safeguards,
transactional demonstration finance and ECharts 6.1.0, receipt/PDF tax snapshots, and separated
list toolbars/row actions. No external payment service or production deployment is included.

## Final automated run
- Isolated source copy, JDK 17, offline Maven `verify`: **359 tests, 0 failures, 0 errors,
  0 skipped**, completed 2026-10-31 13:37 UTC. Live server classes/database were not replaced.
- All seven `tools/check-*.cjs` scripts passed (jsdom supplied as test-only NODE_PATH dependency):
  drug forms, feedback/pending guards, receipts, finance UI, list actions, modal footers,
  workspaces/unsaved changes.
- Reviewed all added commit history for unexpected secrets and artifacts; runtime configs,
  synthetic login fixtures, screenshots, logs, database state and generated PDFs remain outside git.
  Existing upstream configuration/data are unchanged; this is not a historical-secret remediation.

## Actual browser/runtime checks
Actual Spring Boot application, isolated in-memory H2 synthetic data, not static mock screens.
- Administrator add/edit handlers open the correct fixed-footer forms. Delete confirmation cancels
  without deleting. Unsaved-change cancel keeps the draft; explicit discard leaves stored data unchanged.
- Staff finance buttons retain receipt/refund actions; refund dialog cancellation makes no ledger write.
- At 706px viewport administrator actions wrap with separation. Wide legacy tables still need horizontal
  scrolling; this is not a claim of a complete mobile-first administrator redesign.
- Doctor authentication verified using the secure browser credential flow; updated row buttons open
  the correct history and encounter dialogs. Closing/cancelling retains the underlying record.
- Existing real HTTP validation covers all roles, transaction/receipt permissions, PDF and Excel output;
  see finance-validation.md. Current incremental toolbar testing does not recreate financial mutations.
- No JVM or H2 restart during this final toolbar pass.

## Limits and remaining work
- MySQL production-engine concurrency/locking and deployment migrations have **not** been executed;
  H2 transaction tests are not proof of production MySQL equivalence.
- Native hardware printer output not verified. Authenticated PDF content and layout were validated.
- Historical public information templates retain legacy content; runtime screenshot placeholders were
  synthetic only and are not a rewrite of those source pages.
- Screenshots do not exhaust every possible route/state. Some legacy forms need additional business
  validation; details remain in functional-gap-assessment-2026-10-31.md.
- This branch is a reviewable development rebuild, not a production-ready medical/payment platform.
