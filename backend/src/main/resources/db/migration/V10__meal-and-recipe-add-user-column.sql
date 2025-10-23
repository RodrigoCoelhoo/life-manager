ALTER TABLE tb_recipes
ADD COLUMN user_id BIGINT NOT NULL;

ALTER TABLE tb_recipes
ADD CONSTRAINT fk_recipe_user
FOREIGN KEY (user_id) REFERENCES tb_users(id);

ALTER TABLE tb_meals
ADD COLUMN user_id BIGINT NOT NULL;

ALTER TABLE tb_meals
ADD CONSTRAINT fk_meal_user
FOREIGN KEY (user_id) REFERENCES tb_users(id);