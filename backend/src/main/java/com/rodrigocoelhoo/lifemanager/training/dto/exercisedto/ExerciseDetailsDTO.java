package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionExerciseBaseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;

import java.util.List;

public record ExerciseDetailsDTO(
        Long id,
        String name,
        String description,
        String type,
        List<SessionExerciseBaseDTO> sets
) {
    public static ExerciseDetailsDTO fromEntity(ExerciseModel exercise, List<SessionExerciseBaseDTO> sets) {
        return new ExerciseDetailsDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getType().toString(),
                sets
        );
    }
}