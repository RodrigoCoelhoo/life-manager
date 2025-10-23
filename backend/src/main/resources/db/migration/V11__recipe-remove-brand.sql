ALTER TABLE tb_recipe_ingredients
DROP CONSTRAINT IF EXISTS fk_recipe_brand;

ALTER TABLE tb_recipe_ingredients
DROP COLUMN IF EXISTS brand_id;
