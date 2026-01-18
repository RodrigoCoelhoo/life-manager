package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;

import java.io.Serializable;
import java.time.LocalDateTime;

public record SessionSimpleDTO(
    Long id,
    LocalDateTime date
) implements Serializable {
    public static SessionSimpleDTO fromEntity(TrainingSessionModel trainingSessionModel) {
        return new SessionSimpleDTO(
          trainingSessionModel.getId(),
          trainingSessionModel.getDate()
        );
    }
}
