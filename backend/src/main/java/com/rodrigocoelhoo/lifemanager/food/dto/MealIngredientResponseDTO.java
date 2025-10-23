package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.MealIngredientModel;

public record MealIngredientResponseDTO(
        Long ingredientId,
        String ingredient,
        Long brandId,
        String brand,
        Double amount,
        String unit
) {
    public static MealIngredientResponseDTO fromEntity(MealIngredientModel model) {
        return new MealIngredientResponseDTO(
                model.getIngredient().getId(),
                model.getIngredient().getName(),
                model.getBrand().getId(),
                model.getBrand().getName(),
                model.getAmount(),
                model.getUnit().toString()
        );
    }
}
