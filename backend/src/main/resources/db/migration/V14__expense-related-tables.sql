CREATE TABLE tb_wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(15) NOT NULL,
    balance DOUBLE PRECISION NOT NULL CHECK (balance >= 0),
    currency VARCHAR(10) NOT NULL,

    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id)
        REFERENCES tb_users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_wallet_user ON tb_wallets(user_id);


CREATE TABLE tb_transactions(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    amount DOUBLE PRECISION NOT NULL CHECK (amount >= 0.0),
    type VARCHAR(10) NOT NULL,
    description VARCHAR(512) NOT NULL,
    date TIMESTAMP NOT NULL,
    category VARCHAR(30) NOT NULL,

    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id)
            REFERENCES tb_users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id)
            REFERENCES tb_wallets(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_transaction_user ON tb_transactions(user_id);
CREATE INDEX idx_transaction_user_date ON tb_transactions(user_id, date);


CREATE TABLE tb_transferences(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    from_wallet_id BIGINT NOT NULL,
    to_wallet_id BIGINT NOT NULL,
    amount DOUBLE PRECISION NOT NULL CHECK (amount > 0.0),
    date TIMESTAMP NOT NULL,
    description VARCHAR(512) NOT NULL,

    CONSTRAINT fk_transference_user FOREIGN KEY (user_id)
                REFERENCES tb_users(id)
                ON DELETE CASCADE,

    CONSTRAINT fk_transference_from_wallet FOREIGN KEY (from_wallet_id)
            REFERENCES tb_wallets(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_transference_to_wallet FOREIGN KEY (to_wallet_id)
                REFERENCES tb_wallets(id)
                ON DELETE CASCADE
);

CREATE INDEX idx_transference_user ON tb_transferences(user_id);