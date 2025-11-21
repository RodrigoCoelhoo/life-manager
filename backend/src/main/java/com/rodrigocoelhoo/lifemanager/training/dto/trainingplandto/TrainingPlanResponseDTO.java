package com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseSimpleDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;

import java.time.LocalDateTime;
import java.util.List;

public record TrainingPlanResponseDTO(
        Long id,
        String name,
        String description,
        List<ExerciseSimpleDTO> exercises,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TrainingPlanResponseDTO fromEntity(TrainingPlanModel trainingPlanModel) {
        return new TrainingPlanResponseDTO(
                trainingPlanModel.getId(),
                trainingPlanModel.getName(),
                trainingPlanModel.getDescription(),
                trainingPlanModel.getExercises()
                        .stream().map(ExerciseSimpleDTO::fromEntity)
                        .toList(),
                trainingPlanModel.getCreatedAt(),
                trainingPlanModel.getUpdatedAt()
        );
    }
}
