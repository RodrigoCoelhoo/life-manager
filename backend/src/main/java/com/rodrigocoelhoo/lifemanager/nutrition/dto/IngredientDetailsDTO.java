package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public record IngredientDetailsDTO (
        Long id,
        String name,
        List<IngredientBrandDetailsResponseDTO> brands
) implements Serializable {
    public static IngredientDetailsDTO fromEntity(IngredientModel model) {

        Set<IngredientBrandModel> brands = model.getBrands();

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
