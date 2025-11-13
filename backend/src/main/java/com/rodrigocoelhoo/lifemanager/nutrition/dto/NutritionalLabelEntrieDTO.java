package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;

public record NutritionalLabelEntrieDTO(
        String nutrient,
        Double amount,
        String unit
) {
    public static NutritionalLabelEntrieDTO fromEntities(NutritionalTag nutrient, Double amount) {
        return new NutritionalLabelEntrieDTO(
                nutrient.toString(),
                amount,
                nutrient.getUnit()
        );
    }
}
