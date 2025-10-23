package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.RecipeIngredientModel;

public record RecipeIngredientResponseDTO(
        IngredientResponseDTO ingredient,
        Double amount,
        String unit
) {
    public static RecipeIngredientResponseDTO fromEntity(RecipeIngredientModel model) {
        return new RecipeIngredientResponseDTO(
                IngredientResponseDTO.fromEntity(model.getIngredient()),
                model.getAmount(),
                model.getUnit().toString().toLowerCase()
        );
    }
}
