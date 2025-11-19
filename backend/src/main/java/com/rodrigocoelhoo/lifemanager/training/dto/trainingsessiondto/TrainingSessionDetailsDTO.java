package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.time.LocalDateTime;

public record TrainingSessionDetailsDTO(
    Long id,
    TrainingPlanDetailsDTO trainingPlan,
    LocalDateTime date,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TrainingSessionDetailsDTO fromEntity(TrainingSessionModel trainingSessionModel) {
        return new TrainingSessionDetailsDTO(
          trainingSessionModel.getId(),
          TrainingPlanDetailsDTO.fromEntity(trainingSessionModel.getTrainingPlan()),
          trainingSessionModel.getDate(),
          trainingSessionModel.getCreatedAt(),
          trainingSessionModel.getUpdatedAt()
        );
    }
}
