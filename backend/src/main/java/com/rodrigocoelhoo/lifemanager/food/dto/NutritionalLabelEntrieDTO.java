package com.rodrigocoelhoo.lifemanager.food.dto;

import com.rodrigocoelhoo.lifemanager.food.model.NutritionalTag;

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
