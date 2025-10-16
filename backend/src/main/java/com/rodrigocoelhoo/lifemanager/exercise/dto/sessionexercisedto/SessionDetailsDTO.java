package com.rodrigocoelhoo.lifemanager.exercise.dto.sessionexercisedto;

import com.rodrigocoelhoo.lifemanager.exercise.dto.exercisedto.ExerciseDetailsDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingSessionModel;

import java.util.List;

public record SessionDetailsDTO(
    SessionDTO session,
    List<ExerciseDetailsDTO> exercises
) {
    public static SessionDetailsDTO fromEntities(TrainingSessionModel trainingSessionModel, List<ExerciseDetailsDTO> exercisesDTO) {
        return new SessionDetailsDTO(
                SessionDTO.fromEntity(trainingSessionModel),
                exercisesDTO
        );
    }
}
