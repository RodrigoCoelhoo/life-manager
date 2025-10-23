package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.RecipeModel;

import java.util.List;

public record RecipeResponseDTO(
        Long id,
        String name,
        List<IngredientResponseDTO> ingredients
) {
    public static RecipeResponseDTO fromEntity(RecipeModel model) {
        return new RecipeResponseDTO(
                model.getId(),
                model.getName(),
                model.getIngredients().stream()
                        .map(ri -> IngredientResponseDTO.fromEntity(ri.getIngredient()))
                        .toList()
        );
    }
}
