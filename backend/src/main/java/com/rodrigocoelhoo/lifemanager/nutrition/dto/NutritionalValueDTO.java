package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record NutritionalValueDTO(

        NutritionalTag type,

        @NotNull(message = "Value per 100g cannot be null")
        @PositiveOrZero(message = "Nutritional value must be positive")
        Double per100units
) { }
