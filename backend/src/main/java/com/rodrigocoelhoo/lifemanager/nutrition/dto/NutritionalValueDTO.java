package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NutritionalValueDTO(

        @NotBlank(message = "Nutritional type cannot be blank")
        String type,

        @NotNull(message = "Value per 100g cannot be null")
        @Positive(message = "Nutritional value must be positive")
        Double per100units
) { }
