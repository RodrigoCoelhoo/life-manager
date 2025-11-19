package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExerciseUpdateDTO(

    @NotBlank(message = "Exercise name cannot be blank")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    String name,

    @NotNull
    @Size(max = 512, message = "Description cannot exceed 512 characters")
    String description,

    @NotNull
    @Size(max = 2048, message = "demoUrl cannot exceed 2048 characters")
    @Pattern(
            regexp = "^$|^(http|https)://.*$",
            message = "Must be empty or a valid URL starting with http or https"
    )
    String demoUrl
) { }
