package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeModel;

import java.util.List;

public record RecipeDetailsDTO(
        String name,
        List<RecipeIngredientResponseDTO> ingredients
) {
    public static RecipeDetailsDTO fromEntity(RecipeModel model) {
        return new RecipeDetailsDTO(
                model.getName(),
                model.getIngredients().stream()
                        .map(RecipeIngredientResponseDTO::fromEntity)
                        .toList()
        );
    }
}
