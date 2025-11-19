package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.time.LocalDateTime;

public record TrainingSessionResponseDTO(
    Long id,
    TrainingPlanResponseDTO trainingPlan,
    LocalDateTime date,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TrainingSessionResponseDTO fromEntity(TrainingSessionModel trainingSessionModel) {
        return new TrainingSessionResponseDTO(
          trainingSessionModel.getId(),
          TrainingPlanResponseDTO.fromEntity(trainingSessionModel.getTrainingPlan()),
          trainingSessionModel.getDate(),
          trainingSessionModel.getCreatedAt(),
          trainingSessionModel.getUpdatedAt()
        );
    }
}