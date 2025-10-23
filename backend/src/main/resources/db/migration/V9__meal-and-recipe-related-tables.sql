CREATE TABLE tb_recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE tb_recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_recipe FOREIGN KEY (recipe_id) REFERENCES tb_recipes(id) ON DELETE CASCADE,
    CONSTRAINT fk_recipe_ingredient FOREIGN KEY (ingredient_id) REFERENCES tb_ingredients(id),
    CONSTRAINT fk_recipe_brand FOREIGN KEY (brand_id) REFERENCES tb_ingredientbrands(id)
);


CREATE TABLE tb_meals (
    id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL
);

CREATE TABLE tb_meal_ingredients (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_meal FOREIGN KEY (meal_id) REFERENCES tb_meals(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_ingredient FOREIGN KEY (ingredient_id) REFERENCES tb_ingredients(id),
    CONSTRAINT fk_meal_brand FOREIGN KEY (brand_id) REFERENCES tb_ingredientbrands(id)
);
