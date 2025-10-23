package com.rodrigocoelhoo.lifemanager.food.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecipeIngredientDTO(

        @NotNull(message = "Ingredient ID cannot be null")
        Long id,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        Double amount,

        @NotBlank(message = "A Unit must be provided")
        String unit
) { }
