package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SessionExerciseDTO(
        @NotNull(message = "Exercise ID must be provided")
        Long exerciseId,

        @NotNull(message = "Sets list cannot be null, but can be empty")
        List<SessionExerciseSetDTO> sets
) {
}
