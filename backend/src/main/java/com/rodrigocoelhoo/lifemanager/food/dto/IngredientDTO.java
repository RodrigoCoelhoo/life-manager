package com.rodrigocoelhoo.lifemanager.food.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IngredientDTO(

    @NotBlank(message = "Exercise name cannot be blank")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    String name

) { }
