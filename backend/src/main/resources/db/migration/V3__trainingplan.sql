CREATE TABLE tb_trainingplans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_plan_user FOREIGN KEY (user_id)
        REFERENCES tb_users(id)
        ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(512)
);

CREATE INDEX idx_tp_user_id ON tb_trainingplans(user_id);

CREATE TABLE tb_trainingplan_exercises (
    plan_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    PRIMARY KEY (plan_id, exercise_id),

    CONSTRAINT fk_tpe_plan FOREIGN KEY (plan_id)
        REFERENCES tb_trainingplans(id) ON DELETE CASCADE,

    CONSTRAINT fk_tpe_exercise FOREIGN KEY (exercise_id)
        REFERENCES tb_exercises(id) ON DELETE CASCADE
);

CREATE INDEX idx_trainingplan_exercises_plan ON tb_trainingplan_exercises(plan_id);
CREATE INDEX idx_trainingplan_exercises_exercise ON tb_trainingplan_exercises(exercise_id);
