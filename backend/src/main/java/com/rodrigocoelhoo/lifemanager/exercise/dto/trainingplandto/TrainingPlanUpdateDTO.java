package com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TrainingPlanUpdateDTO(

        @NotBlank(message = "Training plan name cannot be empty")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @NotNull(message = "Description cannot be null, but can be empty")
        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description,

        @NotNull(message = "Exercise list cannot be null, but can be empty")
        List<Long> exerciseIds
) { }