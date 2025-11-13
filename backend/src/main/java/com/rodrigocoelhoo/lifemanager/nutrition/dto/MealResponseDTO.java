package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.MealModel;

import java.time.LocalDateTime;
import java.util.List;

public record MealResponseDTO(
        Long id,
        LocalDateTime dateTime,
        List<MealIngredientResponseDTO> ingredients
) {
    public static MealResponseDTO fromEntity(MealModel model) {
        return new MealResponseDTO(
                model.getId(),
                model.getDate(),
                model.getIngredients().stream().map(MealIngredientResponseDTO::fromEntity).toList()
        );
    }
}
