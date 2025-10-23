package com.rodrigocoelhoo.lifemanager.food.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record MealDTO(

        @NotNull(message = "The date the meal was consumed must be provided")
        LocalDateTime date,

        @NotNull(message = "The list of ingredients can't be null, but can be empty")
        List<MealIngredientDTO> ingredients
) { }
