CREATE TABLE tb_ingredients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,

    CONSTRAINT fk_ingredient_user FOREIGN KEY (user_id)
            REFERENCES tb_users(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_ingredients_user_id ON tb_ingredients(user_id);

CREATE TABLE tb_ingredientbrands (
    id BIGSERIAL PRIMARY KEY,
    ingredient_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,

    CONSTRAINT fk_ingredient_brand_ingredient FOREIGN KEY (ingredient_id)
                REFERENCES tb_ingredients(id)
                ON DELETE CASCADE
);

CREATE INDEX idx_ingredientbrands_ingredient_id ON tb_ingredientbrands(ingredient_id);

CREATE TABLE tb_nutritionalvalues (
    id BIGSERIAL PRIMARY KEY,
    ingredient_brand_id BIGINT NOT NULL,
    tag VARCHAR(50) NOT NULL,
    per100units DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_nutritional_value_ingredient_brand FOREIGN KEY (ingredient_brand_id)
                    REFERENCES tb_ingredientbrands(id)
                    ON DELETE CASCADE
);

CREATE INDEX idx_nutritionalvalues_brand_id ON tb_nutritionalvalues(ingredient_brand_id);