CREATE TABLE tb_automatic_transactions(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    type VARCHAR(10) NOT NULL,
    category VARCHAR(30) NOT NULL,
    recurrence VARCHAR(10) NOT NULL,
    recurrence_interval SMALLINT NOT NULL,
    description VARCHAR(512) NOT NULL,
    next_transaction_date DATE NOT NULL,


    CONSTRAINT fk_auto_transaction_user FOREIGN KEY (user_id)
            REFERENCES tb_users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_auto_transaction_wallet FOREIGN KEY (wallet_id)
            REFERENCES tb_wallets(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_auto_transaction_user_date ON tb_automatic_transactions(user_id, next_transaction_date);