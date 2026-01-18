package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseSimpleDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record TrainingSessionResponseDTO(
    Long id,
    LocalDateTime date,
    List<ExerciseSimpleDTO> exercises,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) implements Serializable {
    public static TrainingSessionResponseDTO fromEntity(TrainingSessionModel trainingSessionModel) {

        List<ExerciseSimpleDTO> exercises = trainingSessionModel.getExercises().stream()
                .map(SessionExerciseModel::getExercise)
                .distinct()
                .map(ExerciseSimpleDTO::fromEntity)
                .toList();

        return new TrainingSessionResponseDTO(
            trainingSessionModel.getId(),
            trainingSessionModel.getDate(),
            exercises,
            trainingSessionModel.getCreatedAt(),
            trainingSessionModel.getUpdatedAt()
        );
    }
}