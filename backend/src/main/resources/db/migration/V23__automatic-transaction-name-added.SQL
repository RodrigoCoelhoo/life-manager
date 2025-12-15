ALTER TABLE tb_automatic_transactions
ADD COLUMN name VARCHAR(50);

UPDATE tb_automatic_transactions
SET name = 'Unnamed'
WHERE name IS NULL;

ALTER TABLE tb_automatic_transactions
ALTER COLUMN name SET NOT NULL;
