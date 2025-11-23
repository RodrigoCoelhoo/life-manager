package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.SessionExerciseBaseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;

import java.util.List;

public record ExerciseDetailsDTO(
        Long id,
        String name,
        String type,
        String demoUrl,
        List<SessionExerciseBaseDTO> sets
) {
    public static ExerciseDetailsDTO fromEntity(ExerciseModel exercise, List<SessionExerciseBaseDTO> sets) {
        return new ExerciseDetailsDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getType().toString(),
                exercise.getDemoUrl(),
                sets
        );
    }
}