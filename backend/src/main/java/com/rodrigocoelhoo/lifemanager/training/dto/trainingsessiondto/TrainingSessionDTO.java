package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TrainingSessionDTO(

        @NotNull(message = "Training plan ID must be provided")
        Long trainingPlanId,

        @NotNull(message = "Date of the training session must be provided")
        LocalDateTime date
) { }
