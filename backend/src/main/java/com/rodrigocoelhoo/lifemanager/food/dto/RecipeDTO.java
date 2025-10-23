package com.rodrigocoelhoo.lifemanager.food.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeDTO(

    @NotBlank(message = "Recipe name cannot be blank")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    String name,

    @Valid
    @NotNull(message = "Ingredient list cannot be null, but can be empty")
    List<RecipeIngredientDTO> ingredients
) { }
