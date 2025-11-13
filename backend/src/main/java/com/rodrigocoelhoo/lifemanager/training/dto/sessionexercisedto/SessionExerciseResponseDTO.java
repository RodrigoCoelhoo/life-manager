package com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto;

import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;

public record SessionExerciseResponseDTO(
    Long id,
    Long sessionId,
    Long exercise,
    Integer setNumber,
    Integer reps,
    Double weight,
    Integer durationSecs,
    Integer distance
) {
    public static SessionExerciseResponseDTO fromEntity(SessionExerciseModel model) {
        return new SessionExerciseResponseDTO(
                model.getId(),
                model.getSessionId().getId(),
                model.getExercise().getId(),
                model.getSetNumber(),
                model.getReps(),
                model.getWeight(),
                model.getDurationSecs(),
                model.getDistance()
        );
    }
}
