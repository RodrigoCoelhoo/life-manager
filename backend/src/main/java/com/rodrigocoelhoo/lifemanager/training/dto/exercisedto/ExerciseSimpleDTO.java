package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;

import java.io.Serializable;

public record ExerciseSimpleDTO(
        Long id,
        String name,
        String type,
        String demoUrl
) implements Serializable {
    public static ExerciseSimpleDTO fromEntity(ExerciseModel model) {
        return new ExerciseSimpleDTO(
                model.getId(),
                model.getName(),
                model.getType().toString(),
                model.getDemoUrl()
        );
    }
}
