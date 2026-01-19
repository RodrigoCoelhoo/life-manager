package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;

import java.io.Serializable;

public record IngredientBrandResponseDTO(
        Long id,
        String name
) implements Serializable {
    public static IngredientBrandResponseDTO fromEntity(IngredientBrandModel model) {
        return new IngredientBrandResponseDTO(
                model.getId(),
                model.getName()
        );
    }
}
