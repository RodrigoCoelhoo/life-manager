package com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;

import java.time.LocalDateTime;
import java.util.List;

public record TrainingPlanDetailsDTO(
        Long id,
        String name,
        String description,
        List<ExerciseResponseDTO> exercises,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TrainingPlanDetailsDTO fromEntity(TrainingPlanModel trainingPlanModel) {
        return new TrainingPlanDetailsDTO(
            trainingPlanModel.getId(),
            trainingPlanModel.getName(),
            trainingPlanModel.getDescription(),
            trainingPlanModel.getExercises() == null ? List.of() :
                    trainingPlanModel.getExercises().stream()
                    .map(ExerciseResponseDTO::fromEntity)
                    .toList(),
            trainingPlanModel.getCreatedAt(),
            trainingPlanModel.getUpdatedAt()
        );
    }
}
