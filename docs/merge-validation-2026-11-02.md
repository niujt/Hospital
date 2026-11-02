# Merge validation — 2026-11-02

## Source and branch safety
- Fresh isolated macOS checkout of niujt/Hospital; default branch confirmed as `master`.
- Starting master: `7303f8e722cf15753bfddad3408895e92d39dd42`.
- Rebuild: `9e026c66a04fe01d6047ef6118b16b27fec2a397`; one commit ahead, zero behind at inspection.
- No repository AGENTS.md, .agents or .codex instructions were present.
- GitHub reported master unprotected and no repository rulesets. No protection settings changed.

## Executed regression
- JDK 17.0.17, Maven 3.8.4, `mvn -o -B verify`: **359 tests, 0 failures, 0 errors, 0 skipped**, BUILD SUCCESS.
- All seven existing `tools/check-*.cjs` checks passed. jsdom 26 was supplied from a temporary test-only install; no production dependency changed.
- Initial JDK 8 run could not compile existing tests using String.repeat and Files.readString; rerun on JDK 17 passed. This is not a Java 8 test-suite compatibility claim.
- Real Spring Boot server on isolated local port 8090, H2 2.1.214 MySQL mode, complete legacy schema and explicitly enabled finance migrations. Runtime configuration and fixtures stayed outside git; original MySQL configuration was not changed.
- Real HTTP workflow passed: patient/admin/doctor authentication, payment password setup, simulated recharge 500.00 and admin confirmation, 10% drug commission policy, completed encounter, doctor order, patient wallet payment 25.00, partial refund 5.00, procurement 80.00, authenticated receipt PDF, cash and doctor reports.
- Result checks: patient balance **480.00**; cash income **500.00**, expense **80.00**, net **420.00**; completed encounters **1**, net commission **2.00**.
- Actual browser login passed for all three roles. Patient appointment/wallet pages, doctor encounter/statistics pages, admin overview/medication tab/finance navigation rendered successfully.
- Browser console had an existing missing favicon 404 and informational font/accessibility messages; no application JavaScript errors were observed in the inspected flows.

## Screenshot verification
- Twelve PNGs in `img/current/`, captured using Playwright page/element screenshots at 1440 × 1000 viewport (full page where applicable).
- Individually opened and reviewed: homepage, blank login, patient portal, appointment, wallet, doctor workbench/statistics, admin overview, tabbed medication inventory, finance full page and both chart sections.
- Mouse moved to (0,0); page screenshots exclude the system cursor. No pointer, debug overlay, password, real patient details or keys appear in delivered screenshots.
- README uses repository-relative image links; old historical image files are retained but no longer presented as current UI.

## Limits
- Production MySQL engine/concurrency/migrations were not executed; H2 checks do not establish MySQL equivalence.
- No Docker, external database, production deployment, actual payments or hardware printing used.
- No CI workflow exists in the inspected source; local validation is the evidence here. Remote commit/check status is verified separately after push.
