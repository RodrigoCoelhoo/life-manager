DROP INDEX IF EXISTS idx_auto_transaction_user_date;

CREATE INDEX idx_auto_transaction_user ON tb_automatic_transactions(user_id);

CREATE INDEX idx_auto_transaction_date ON tb_automatic_transactions(next_transaction_date);
