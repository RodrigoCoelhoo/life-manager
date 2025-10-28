ALTER TABLE tb_transactions
    ALTER COLUMN date TYPE DATE USING date::DATE;

ALTER TABLE tb_transferences
    ALTER COLUMN date TYPE DATE USING date::DATE;