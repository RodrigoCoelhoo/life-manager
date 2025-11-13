package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalValueModel;

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
