CREATE TABLE IF NOT EXISTS tb_exercises (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    CONSTRAINT fk_exercise_user FOREIGN KEY (user_id)
        REFERENCES tb_users(id)
        ON DELETE CASCADE,

    name VARCHAR(50) NOT NULL,
    description VARCHAR(512),
    demo_url VARCHAR(2048),
    type VARCHAR(50) NOT NULL, -- SET_REP or TIME

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_exercises_user_id ON tb_exercises(user_id);
