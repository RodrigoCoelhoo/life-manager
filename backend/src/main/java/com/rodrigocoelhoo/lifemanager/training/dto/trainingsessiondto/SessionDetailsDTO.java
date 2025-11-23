package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.util.List;

public record SessionDetailsDTO(
    SessionSimpleDTO session,
    List<ExerciseDetailsDTO> exercises
) {
    public static SessionDetailsDTO fromEntities(TrainingSessionModel trainingSessionModel, List<ExerciseDetailsDTO> exercisesDTO) {
        return new SessionDetailsDTO(
                SessionSimpleDTO.fromEntity(trainingSessionModel),
                exercisesDTO
        );
    }
}
