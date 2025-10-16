package com.rodrigocoelhoo.lifemanager.exercise.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingSessionModel;

import java.time.LocalDateTime;

public record TrainingSessionResponseDTO(
    Long sessionId,
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