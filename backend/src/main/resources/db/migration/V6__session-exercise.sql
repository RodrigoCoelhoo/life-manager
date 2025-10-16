CREATE TABLE tb_session_exercises (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,

    -- SET_REP fields
    set_number INTEGER,
    reps INTEGER,
    weight NUMERIC(6,2),

    -- TIME fields
    duration_secs INTEGER,
    distance INTEGER,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_session
        FOREIGN KEY(session_id)
        REFERENCES tb_trainingsessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_exercise
        FOREIGN KEY(exercise_id)
        REFERENCES tb_exercises(id)
        ON DELETE CASCADE
);
