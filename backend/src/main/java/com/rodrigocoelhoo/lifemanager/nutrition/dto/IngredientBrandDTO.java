package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record IngredientBrandDTO(
        @NotBlank(message = "Brand name cannot be blank")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @Valid
        @NotNull(message = "Nutritional values list cannot be null, but can be empty")
        List<NutritionalValueDTO> nutritionalValues
) {}
