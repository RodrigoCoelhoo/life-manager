package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record TrainingSessionDTO(
        @NotNull(message = "Date of the training session must be provided")
        LocalDateTime date,

        @NotNull(message = "Exercise list cannot be null, but can be empty")
        List<SessionExerciseDTO> exercises
) { }
