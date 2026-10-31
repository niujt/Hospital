-- Additive, rerunnable opt-in migration. Does not rewrite historical orders/sales.
-- Newly initialized policy uses the explicitly requested 3% inclusive demo rate.
-- Existing policy rows are never overwritten by re-running this migration.
-- Missing snapshots represent an explicitly unconfigured 0% demo split, never tax exemption.
CREATE TABLE IF NOT EXISTS finance_tax_policy (
 id INT PRIMARY KEY, rate DECIMAL(7,4) NOT NULL DEFAULT 0,
 configured BOOLEAN NOT NULL DEFAULT FALSE, updated_by INT NULL, updated_at TIMESTAMP NULL,
 CONSTRAINT ck_demo_tax_rate CHECK(rate >= 0 AND rate <= 100)
) ENGINE=InnoDB;
INSERT INTO finance_tax_policy(id,rate,configured) SELECT 1,3,TRUE
 WHERE NOT EXISTS (SELECT 1 FROM finance_tax_policy WHERE id=1);
CREATE TABLE IF NOT EXISTS finance_order_tax (
 order_id VARCHAR(36) PRIMARY KEY, sale_id VARCHAR(36) NULL UNIQUE,
 rate DECIMAL(7,4) NOT NULL, configured BOOLEAN NOT NULL,
 gross_amount DECIMAL(19,2) NOT NULL, net_amount DECIMAL(19,2) NOT NULL, tax_amount DECIMAL(19,2) NOT NULL,
 CONSTRAINT fk_demo_tax_order FOREIGN KEY(order_id) REFERENCES finance_order(id),
 CONSTRAINT fk_demo_tax_sale FOREIGN KEY(sale_id) REFERENCES finance_sale(id),
 CONSTRAINT ck_demo_snapshot_rate CHECK(rate >= 0 AND rate <= 100),
 CONSTRAINT ck_demo_tax_split CHECK(gross_amount = net_amount + tax_amount AND net_amount >= 0 AND tax_amount >= 0)
) ENGINE=InnoDB;
