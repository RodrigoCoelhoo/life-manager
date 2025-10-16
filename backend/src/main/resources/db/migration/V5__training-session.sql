CREATE TABLE tb_trainingsessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    training_plan BIGINT,
    date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES tb_users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_training_plan
        FOREIGN KEY(training_plan)
        REFERENCES tb_trainingplans(id)
        ON DELETE SET NULL
);
