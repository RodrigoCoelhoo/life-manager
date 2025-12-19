package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalValueModel;

import java.util.Comparator;
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
                .sorted(Comparator.comparingInt(dto -> dto.nutrient().ordinal()))
                .toList();

        return new IngredientBrandDetailsResponseDTO(
                model.getId(),
                model.getName(),
                nutrientsDTO
        );
    }
}
