-- OPT-IN ONLY. Back up and review before applying to an existing MySQL database.
-- Compatible with MySQL and H2 MySQL mode for isolated tests. No legacy data rewritten.
CREATE TABLE IF NOT EXISTS finance_operation (
 id VARCHAR(36) PRIMARY KEY, actor_id INT NOT NULL, request_key VARCHAR(128) NOT NULL,
 fingerprint VARCHAR(64) NOT NULL, result_json TEXT, created_at TIMESTAMP NOT NULL,
 CONSTRAINT uq_finance_request UNIQUE(actor_id, request_key)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_wallet (
 patient_id INT PRIMARY KEY, balance DECIMAL(19,2) NOT NULL DEFAULT 0,
 CONSTRAINT fk_wallet_patient FOREIGN KEY(patient_id) REFERENCES patient(id),
 CONSTRAINT ck_wallet_balance CHECK (balance >= 0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_recharge (
 id VARCHAR(36) PRIMARY KEY, patient_id INT NOT NULL, amount DECIMAL(19,2) NOT NULL,
 channel VARCHAR(16) NOT NULL, status VARCHAR(16) NOT NULL, requested_at TIMESTAMP NOT NULL,
 confirmed_at TIMESTAMP NULL, confirmed_by INT NULL,
 CONSTRAINT fk_recharge_patient FOREIGN KEY(patient_id) REFERENCES patient(id),
 CONSTRAINT ck_recharge_amount CHECK (amount > 0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_drug_policy (
 drug_id INT PRIMARY KEY, commission_rate DECIMAL(7,4) NOT NULL DEFAULT 0,
 updated_by INT NOT NULL, updated_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_policy_drug FOREIGN KEY(drug_id) REFERENCES drugs(id),
 CONSTRAINT ck_commission_rate CHECK (commission_rate >= 0 AND commission_rate <= 100)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_encounter (
 id VARCHAR(36) PRIMARY KEY, appointment_id INT NOT NULL UNIQUE, patient_id INT NOT NULL,
 doctor_id INT NOT NULL, completed_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_encounter_appointment FOREIGN KEY(appointment_id) REFERENCES appointment(id),
 CONSTRAINT fk_encounter_patient FOREIGN KEY(patient_id) REFERENCES patient(id),
 CONSTRAINT fk_encounter_doctor FOREIGN KEY(doctor_id) REFERENCES doctor(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_sale (
 id VARCHAR(36) PRIMARY KEY, patient_id INT NOT NULL, drug_id INT NOT NULL,
 quantity INT NOT NULL, unit_price DECIMAL(19,2) NOT NULL, amount DECIMAL(19,2) NOT NULL,
 payment_method VARCHAR(16) NOT NULL, encounter_id VARCHAR(36) NULL, doctor_id INT NULL,
 commission_rate DECIMAL(7,4) NOT NULL, commission DECIMAL(19,2) NOT NULL,
 refunded_amount DECIMAL(19,2) NOT NULL DEFAULT 0, reversed_commission DECIMAL(19,2) NOT NULL DEFAULT 0,
 created_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_sale_patient FOREIGN KEY(patient_id) REFERENCES patient(id),
 CONSTRAINT fk_sale_drug FOREIGN KEY(drug_id) REFERENCES drugs(id),
 CONSTRAINT fk_sale_encounter FOREIGN KEY(encounter_id) REFERENCES finance_encounter(id),
 CONSTRAINT ck_sale_quantity CHECK (quantity > 0),
 CONSTRAINT ck_sale_refund CHECK (refunded_amount >= 0 AND refunded_amount <= amount)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_refund (
 id VARCHAR(36) PRIMARY KEY, sale_id VARCHAR(36) NOT NULL, amount DECIMAL(19,2) NOT NULL,
 reversed_commission DECIMAL(19,2) NOT NULL, reason VARCHAR(500) NOT NULL,
 actor_id INT NOT NULL, created_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_refund_sale FOREIGN KEY(sale_id) REFERENCES finance_sale(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_procurement (
 id VARCHAR(36) PRIMARY KEY, drug_id INT NOT NULL, quantity INT NOT NULL,
 unit_cost DECIMAL(19,2) NOT NULL, amount DECIMAL(19,2) NOT NULL,
 reference_text VARCHAR(200) NOT NULL, actor_id INT NOT NULL, created_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_procurement_drug FOREIGN KEY(drug_id) REFERENCES drugs(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_ledger (
 id VARCHAR(36) PRIMARY KEY, operation_id VARCHAR(36) NOT NULL, book VARCHAR(24) NOT NULL,
 direction VARCHAR(3) NOT NULL, category VARCHAR(32) NOT NULL,
 patient_id INT NULL, amount DECIMAL(19,2) NOT NULL, source_id VARCHAR(36) NOT NULL,
 original_source_id VARCHAR(36) NULL, actor_id INT NOT NULL, created_at TIMESTAMP NOT NULL,
 CONSTRAINT fk_ledger_operation FOREIGN KEY(operation_id) REFERENCES finance_operation(id),
 CONSTRAINT ck_ledger_amount CHECK(amount > 0),
 CONSTRAINT ck_ledger_book CHECK(book IN ('CASH','PATIENT_BALANCE')),
 CONSTRAINT ck_ledger_direction CHECK(direction IN ('IN','OUT'))
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_order (
 id VARCHAR(36) PRIMARY KEY, patient_id INT NOT NULL, drug_id INT NOT NULL, quantity INT NOT NULL,
 unit_price DECIMAL(19,2) NOT NULL, amount DECIMAL(19,2) NOT NULL,
 payment_method VARCHAR(16) NOT NULL, encounter_id VARCHAR(36) NULL,
 created_by INT NOT NULL, status VARCHAR(16) NOT NULL, sale_id VARCHAR(36) NULL,
 created_at TIMESTAMP NOT NULL, paid_at TIMESTAMP NULL,
 CONSTRAINT fk_order_patient FOREIGN KEY(patient_id) REFERENCES patient(id),
 CONSTRAINT fk_order_drug FOREIGN KEY(drug_id) REFERENCES drugs(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_payment_security (
 patient_id INT PRIMARY KEY, salt VARCHAR(64) NOT NULL, password_hash VARCHAR(128) NOT NULL, iterations INT NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS finance_demo_card (
 id VARCHAR(36) PRIMARY KEY, patient_id INT NOT NULL, issuer VARCHAR(80) NOT NULL,
 last_four VARCHAR(4) NOT NULL, created_at TIMESTAMP NOT NULL
) ENGINE=InnoDB;
-- Serialize name validation within the finance catalog APIs without rewriting legacy drug rows.
CREATE TABLE IF NOT EXISTS finance_catalog_guard (id INT PRIMARY KEY) ENGINE=InnoDB;
INSERT INTO finance_catalog_guard(id) SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM finance_catalog_guard WHERE id=1);
