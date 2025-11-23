ALTER TABLE tb_trainingsessions
DROP CONSTRAINT IF EXISTS fk_training_plan;

ALTER TABLE tb_trainingsessions
DROP COLUMN training_plan;
