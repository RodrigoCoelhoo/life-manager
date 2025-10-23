package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.NutritionalValueModel;

public record NutritionalValueResponseDTO(
        Long id,
        String nutrient,
        Double per100units,
        String unit
) {
    public static NutritionalValueResponseDTO fromEntity(NutritionalValueModel model) {
        return new NutritionalValueResponseDTO(
                model.getId(),
                model.getTag().toString(),
                model.getPer100units(),
                model.getTag().getUnit()
        );
    }
}
