package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExerciseDTO(

        @NotBlank(message = "Exercise name cannot be blank")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @NotNull(message = "Description cannot be null, but can be empty")
        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description,

        @NotBlank(message = "Exercise type cannot be blank")
        @Pattern(
                regexp = "^(SET_REP|TIME)$",
                message = "Exercise type must be either 'SET_REP' or 'TIME'"
        )
        String type,

        @NotNull(message = "demoUrl cannot be null, but can be empty")
        @Size(max = 2048, message = "demoUrl cannot exceed 2048 characters")
        @Pattern(
                regexp = "^(http|https)://.*$",
                message = "Must be a valid URL starting with http or https"
        )
        String demoUrl
) { }