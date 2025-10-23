package com.rodrigocoelhoo.lifemanager.food.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MealIngredientDTO(

        @NotNull(message = "Ingredient ID must be provided")
        Long ingredientId,

        @NotNull(message = "Ingredient Brand ID must be provided")
        Long brandId,

        @NotNull(message = "Amount of each ingredient must be provided")
        @Positive(message = "Amount must be positive")
        Double amount,

        @NotNull(message = "Unit must be provided")
        String unit
) {}
