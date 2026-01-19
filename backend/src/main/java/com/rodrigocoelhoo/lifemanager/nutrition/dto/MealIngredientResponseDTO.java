package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.MealIngredientModel;

import java.io.Serializable;

public record MealIngredientResponseDTO(
        Long ingredientId,
        String ingredient,
        Long brandId,
        String brand,
        Double amount,
        String unit
) implements Serializable {
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
