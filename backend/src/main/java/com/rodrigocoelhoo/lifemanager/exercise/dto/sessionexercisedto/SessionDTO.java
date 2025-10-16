package com.rodrigocoelhoo.lifemanager.exercise.dto.sessionexercisedto;

import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingSessionModel;

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
