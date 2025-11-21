package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;

public record ExerciseSimpleDTO(
        Long id,
        String name,
        String type
) {
    public static ExerciseSimpleDTO fromEntity(ExerciseModel model) {
        return new ExerciseSimpleDTO(
                model.getId(),
                model.getName(),
                model.getType().toString()
        );
    }
}
