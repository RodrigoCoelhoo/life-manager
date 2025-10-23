package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.IngredientBrandModel;

public record IngredientBrandResponseDTO(
        Long id,
        String name
) {
    public static IngredientBrandResponseDTO fromEntity(IngredientBrandModel model) {
        return new IngredientBrandResponseDTO(
                model.getId(),
                model.getName()
        );
    }
}
