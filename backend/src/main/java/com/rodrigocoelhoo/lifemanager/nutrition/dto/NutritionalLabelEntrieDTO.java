package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;

import java.io.Serializable;

public record NutritionalLabelEntrieDTO(
        String nutrient,
        Double amount,
        String unit
) implements Serializable {
    public static NutritionalLabelEntrieDTO fromEntities(NutritionalTag nutrient, Double amount) {
        return new NutritionalLabelEntrieDTO(
                nutrient.toString(),
                amount,
                nutrient.getUnit()
        );
    }
}
