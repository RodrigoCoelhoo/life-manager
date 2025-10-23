package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.food.model.NutritionalValueModel;

import java.util.List;

public record IngredientBrandDetailsResponseDTO(
        Long id,
        String name,
        List<NutritionalValueResponseDTO> nutritionalValues
) {
    public static IngredientBrandDetailsResponseDTO fromEntity(IngredientBrandModel model) {

        List<NutritionalValueModel> nutrients = model.getNutritionalValues();

        List<NutritionalValueResponseDTO> nutrientsDTO = nutrients.stream()
                .map(NutritionalValueResponseDTO::fromEntity)
                .toList();

        return new IngredientBrandDetailsResponseDTO(
                model.getId(),
                model.getName(),
                nutrientsDTO
        );
    }
}
