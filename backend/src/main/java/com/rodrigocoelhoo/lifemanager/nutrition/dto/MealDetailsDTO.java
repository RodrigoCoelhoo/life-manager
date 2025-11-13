package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.MealModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;

import java.util.LinkedHashMap;
import java.util.List;

public record MealDetailsDTO(
        MealResponseDTO meal,
        List<NutritionalLabelEntrieDTO> nutritionalLabel
) {

    public static MealDetailsDTO fromEntities(MealModel meal, LinkedHashMap<NutritionalTag, Double> nutritionalLabel) {
        return new MealDetailsDTO(
                MealResponseDTO.fromEntity(meal),
                nutritionalLabel.entrySet().stream()
                        .map(entry -> NutritionalLabelEntrieDTO.fromEntities(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }
}
