package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeIngredientModel;

import java.io.Serializable;

public record RecipeIngredientResponseDTO(
        IngredientResponseDTO ingredient,
        Double amount,
        String unit
) implements Serializable {
    public static RecipeIngredientResponseDTO fromEntity(RecipeIngredientModel model) {
        return new RecipeIngredientResponseDTO(
                IngredientResponseDTO.fromEntity(model.getIngredient()),
                model.getAmount(),
                model.getUnit().toString().toLowerCase()
        );
    }
}
