package com.rodrigocoelhoo.lifemanager.exercise.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanDetailsDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingSessionModel;

import java.time.LocalDateTime;

public record TrainingSessionDetailsDTO(
    Long sessionId,
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
