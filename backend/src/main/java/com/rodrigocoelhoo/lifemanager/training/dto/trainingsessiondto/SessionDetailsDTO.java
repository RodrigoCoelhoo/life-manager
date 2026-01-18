package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.io.Serializable;
import java.util.List;

public record SessionDetailsDTO(
    SessionSimpleDTO session,
    List<ExerciseDetailsDTO> exercises
) implements Serializable {
    public static SessionDetailsDTO fromEntities(TrainingSessionModel trainingSessionModel, List<ExerciseDetailsDTO> exercisesDTO) {
        return new SessionDetailsDTO(
                SessionSimpleDTO.fromEntity(trainingSessionModel),
                exercisesDTO
        );
    }
}
