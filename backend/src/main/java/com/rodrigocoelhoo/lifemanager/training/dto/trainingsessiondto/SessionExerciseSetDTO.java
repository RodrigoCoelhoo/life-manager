package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseType;

public record SessionExerciseSetDTO(
        Integer setNumber,
        Integer reps,
        Double weight,

        Integer durationSecs,
        Integer distance
) {
    public static void validateForType(SessionExerciseSetDTO data, ExerciseType type) {
        if ("SET_REP".equals(type.toString())) {
            if (data.setNumber() == null || data.reps() == null || data.weight() == null) {
                throw new BadRequestException("Exercise with type 'SET_REP' must have 'setNumber', 'reps' and 'weight' fields");
            }
        } else {
            if (data.durationSecs() == null || data.distance() == null) {
                {
                    throw new BadRequestException("Exercise with type 'TIME' must have 'durationSecs' and 'distance' fields");
                }
            }
        }
    }
}