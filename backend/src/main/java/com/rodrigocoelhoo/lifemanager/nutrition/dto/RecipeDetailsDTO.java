package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeModel;

import java.io.Serializable;
import java.util.List;

public record RecipeDetailsDTO(
        Long id,
        String name,
        List<RecipeIngredientResponseDTO> ingredients
) implements Serializable {
    public static RecipeDetailsDTO fromEntity(RecipeModel model) {
        return new RecipeDetailsDTO(
                model.getId(),
                model.getName(),
                model.getIngredients().stream()
                        .distinct()
                        .map(RecipeIngredientResponseDTO::fromEntity)
                        .toList()
        );
    }
}
