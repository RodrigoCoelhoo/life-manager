package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;

import java.time.LocalDateTime;

public record ExerciseResponseDTO(
        Long id,
        String name,
        String description,
        String type,
        String demoUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ExerciseResponseDTO fromEntity(ExerciseModel model) {
        return new ExerciseResponseDTO(
                model.getId(),
                model.getName(),
                model.getDescription()  != null ? model.getDescription() : "",
                model.getType().toString(),
                model.getDemoUrl() != null ? model.getDemoUrl() : "",
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}

