package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseType;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ExerciseResponseDTO(
        Long id,
        String name,
        String description,
        ExerciseType type,
        String demoUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
    public static ExerciseResponseDTO fromEntity(ExerciseModel model) {
        return new ExerciseResponseDTO(
                model.getId(),
                model.getName(),
                model.getDescription()  != null ? model.getDescription() : "",
                model.getType(),
                model.getDemoUrl() != null ? model.getDemoUrl() : "",
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}

