# Finance API contract v2 (active implementation in feature/local-finance only)

All paths below start `/api/finance`. Trusted login is normal `/login`; roles 1 admin,
2 doctor, 3 patient, reloaded from DB. Client role/id never authorizes an operation.
GET `/session` returns `{csrfToken}`. Every mutation sends `X-CSRF-Token`; business mutations
also require `Idempotency-Key` (new UUID for a new intent, reuse on retries/lost responses).
JSON bodies. Monetary amounts are decimal **strings**, two places; rates percent strings up to
4 places. Errors actual HTTP 400/401/403/404/409/500 with `{message}` (interceptor errors may
use framework error shape). Never display backend exception/SQL details.

## Safety labels
All recharge channels and cards are **SIMULATED**. No real payment API. System payment password
is independent of login and NOT any real WeChat/Alipay/bank password. Never ask full card number,
CVV, bank login or real payment password. No static demoCode remains.

## Security and cards (patient only)
- GET `/payment-password` -> `{configured:boolean}`.
- PUT `/payment-password` `{loginPassword,currentPaymentPassword,newPaymentPassword}`.
  Initial setup verifies loginPassword; change verifies currentPaymentPassword. New password
  6..32 chars. Do not cache/log these values, clear controls after request. No hash is returned.
- GET `/cards` -> `[{id,issuer,maskedNumber,demo:true,label}]`, IDs are UUID strings. POST `/cards` `{issuer,lastFour}`:
  issuer demo label, lastFour exactly four digits; DELETE `/cards/{id}` own card only.
  POST/card mutation does not use the business-ledger idempotency mechanism; do not retry blindly.

## Patient wallet and recharge
- GET `/me` -> `{patientId,balance}`.
- GET `/balance?patientId=...` admin only, same DTO.
- POST `/recharges` `{amount,channel,paymentPassword,cardId?}` ->
  `{id,status:'PENDING',amount,channel}`. channel WECHAT / ALIPAY / BANK; BANK needs own mock card.
- GET `/recharges`: patient own/admin all, array of
  `{id,patientId,amount,channel,status,requestedAt}`. Refresh pending status; don't claim credited.
- POST `/recharges/{id}/confirm` admin only, no body -> confirmed result. Repeated confirmation
  with different keys remains one credit due to recharge-row lock/state.

## Catalog, policies, patient selectors
- GET `/catalog` authenticated, array `{drugId,name,stock,price}` **never commission**.
- GET `/drug-policies` admin/doctor -> same fields plus `commissionRate`.
- PUT `/drug-policies/{id}` admin `{commissionRate}` -> `{drugId,commissionRate}`.
  Default 0 for an unset policy. Patient endpoints must not display staff policy response.
- GET `/patients` admin -> `[{patientId,name}]`; GET `/doctors` admin -> `[{doctorId,name}]`.

## Orders and payment (separate state prevents double payment with different keys)
- POST `/orders` `{patientId?,drugId,quantity,paymentMethod,encounterId?}` ->
  `{id,status:'PENDING',amount,paymentMethod}`. PatientId derived from session for patient;
  staff supplies patient. WALLET for patient/doctor; CASH admin only.
- GET `/orders` patient own/doctor created/admin all ->
  `[{id,patientId,drugId,quantity,amount,status,saleId,paymentMethod}]`.
- POST `/orders/{id}/pay` `{paymentPassword}` -> `{id,saleId,status:'PAID',amount}`.
  WALLET requires patient role and own SYSTEM password; doctors only create pending orders, never collect patient passwords. CASH admin confirms physical/manual receipt.
  Disable submit while pending, retain order ID/key on retry; backend independently locks order.
- GET `/sales` patient own/doctor attributed/admin all ->
  `[{id,patientId,drugId,quantity,amount,refundedAmount,paymentMethod,createdAt}]`.
  Staff additionally sees doctorId/commissionRate/commission/reversedCommission.
- POST `/sales/{id}/refund` admin `{amount,reason}` ->
  `{id,saleId,amount,destination:'BALANCE',status:'COMPLETED'}`.
  Patient UI says contact administrator to request refund, not fake self-service.
  All refunds return wallet balance; no cash outflow and no assumed physical stock return.

## Procurement (admin)
- GET `/procurements` -> `[{id,drugId,drugName,quantity,unitCost,amount,reference,createdAt}]` actual purchase details.
- POST `/procurements` `{drugId,quantity,unitCost,reference}` -> `{id,amount,status}`.
  Real test ledger cash OUT and inventory IN in one transaction, no external payment call.

## Completed encounters and commission
- GET `/encounters/appointments` doctor own/admin all (optional admin doctorId) ->
  `[{appointmentId,patientId,patientName,doctorId,doctorName,date,encounterId,status}]`.
- POST `/encounters/complete` doctor `{appointmentId}` -> `{id,status:'COMPLETED'}`.
  Explicit actual-completion button, not pageview. Assigned doctor only, one encounter per
  appointment; future appointments cannot be marked complete. Count timestamp is completion.
- GET `/encounters` patient own/doctor own/admin all ->
  `[{id,appointmentId,patientId,doctorId,completedAt}]`.
- Orders may link to an owned completed encounter. Doctor sales require own encounter;
  unattributed standalone sales earn zero commission. Rate and commission snapshot on payment.
  Refund reverses cumulative prorata original commission HALF_UP, final refund clears remainder.

## Actual ECharts aggregates and ledgers
- GET `/patient-transactions` patient own/admin all or admin `?patientId=` ->
  `[{id,patientId,book,direction,category,amount,sourceId,originalSourceId,createdAt}]`.
  Recharge shown once via balance credit; wallet purchases OUT; cash purchases shown as cash
  purchase records; refund IN. Never sum cash and balance books as hospital cash receipts.
- GET `/cash-transactions` admin, same DTO, actual CASH book only.
- GET `/reports/cash?period=DAY|WEEK|MONTH` admin ->
  `{period,timezone:'Asia/Shanghai',points:[{period,income,expense,net}]}`.
- GET `/reports/doctor?period=DAY|WEEK|MONTH&doctorId=...` doctor ignores supplied doctorId and
  uses own; admin must select doctorId ->
  `{doctorId,period,timezone:'Asia/Shanghai',points:[{period,encounters,commission}]}`.
- Weeks start Monday; month labels YYYY-MM; day/week-start labels YYYY-MM-DD. Empty data returns
  `points:[]`, not fabricated chart examples. Commission may be negative in a refund period.

Database opt-in: `hospital.finance.initialize-schema=true` only reviewed/isolated environment;
SQL `src/main/resources/db/finance/V1__finance.sql`. No real database was applied by this change.

## Existing drug form integration (admin only)
- POST `/drugs` and PUT `/drugs/{drugId}` atomically save metadata and independent commission policy.
- JSON `{name,type,price,text,commissionRate}`; type integer 0..5; positive price <=99999999.99;
  text optional <=2000 chars; commission percent 0..100 with at most four decimals.
- Same CSRF and Idempotency-Key rules. Response `{drugId,name,type,price,text,stock,commissionRate,message}`.
- New stock is always0; editing never changes stock. No `number` input accepted into the service.
- Staff GET `/drug-policies` includes `type` and `text` for the existing edit form. Patient catalog
  remains a separate safe DTO without any commission field.
- Name validation serialized within these finance APIs; policy failure rolls back metadata.
- Existing patient appointment POST now derives patientId from trusted login/profile rather than
  accepting another patient's ID in its body. The legacy max-id linking workflow is otherwise unchanged.

## Administrator-assigned doctor accounts
- POST `/doctor-accounts` admin only `{doctorId,username,password}`, CSRF + Idempotency-Key.
  Username 3..64 letters/digits/_.-; new password 8..72 characters. Response
  `{doctorId,loginId,username,accountAssigned:true,message}`; never returns password/hash.
- GET `/doctors` includes `accountAssigned` to select only unbound doctor profiles.
- Existing bound profiles cannot be rebound. Doctor account creation and profile binding are
  one transaction. Identical retries return same assignment; changed secret under same key is rejected.
- New staff credentials use salted PBKDF2; existing login credential format remains compatible.
  Admin doctor edit receives blank password; blank means unchanged, a new value is hashed.
- Public registration cannot create or claim doctor accounts; patients still need an existing
  unbound patient record. This does not silently self-create a medical patient archive.
- Public doctor search and department lookup return only `id,name,department,expert,text`.
