package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;

import java.util.List;

public record IngredientDetailsDTO (
        Long id,
        String name,
        List<IngredientBrandDetailsResponseDTO> brands
) {
    public static IngredientDetailsDTO fromEntity(IngredientModel model) {

        List<IngredientBrandModel> brands = model.getBrands();

        List<IngredientBrandDetailsResponseDTO> brandsDTO = brands.stream()
                .map(IngredientBrandDetailsResponseDTO::fromEntity)
                .toList();

        return new IngredientDetailsDTO(
                model.getId(),
                model.getName(),
                brandsDTO
        );
    }
}
