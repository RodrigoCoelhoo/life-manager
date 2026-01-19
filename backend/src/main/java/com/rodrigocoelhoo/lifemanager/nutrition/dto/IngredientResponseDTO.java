package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;

import java.io.Serializable;
import java.util.List;

public record IngredientResponseDTO (
    Long id,
    String name,
    List<IngredientBrandResponseDTO> brands
) implements Serializable {
    public static IngredientResponseDTO fromEntity(IngredientModel ingredientModel) {
        return new IngredientResponseDTO(
                ingredientModel.getId(),
                ingredientModel.getName(),
                ingredientModel.getBrands().stream()
                        .map(IngredientBrandResponseDTO::fromEntity)
                        .toList()
        );
    }
}