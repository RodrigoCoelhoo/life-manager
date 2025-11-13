package com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto;

import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.time.LocalDateTime;

public record SessionDTO(
    Long id,
    TrainingPlanResponseDTO trainingPlan,
    LocalDateTime date
) {
    public static SessionDTO fromEntity(TrainingSessionModel trainingSessionModel) {
        return new SessionDTO(
          trainingSessionModel.getId(),
          TrainingPlanResponseDTO.fromEntity(trainingSessionModel.getTrainingPlan()),
          trainingSessionModel.getDate()
        );
    }
}
