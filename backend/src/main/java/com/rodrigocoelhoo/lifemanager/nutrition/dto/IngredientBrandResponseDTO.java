package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;

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
