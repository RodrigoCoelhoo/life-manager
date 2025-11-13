package com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto;

import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;

import java.time.LocalDateTime;

public record TrainingPlanResponseDTO(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TrainingPlanResponseDTO fromEntity(TrainingPlanModel trainingPlanModel) {
        return new TrainingPlanResponseDTO(
                trainingPlanModel.getId(),
                trainingPlanModel.getName(),
                trainingPlanModel.getDescription(),
                trainingPlanModel.getCreatedAt(),
                trainingPlanModel.getUpdatedAt()
        );
    }
}
