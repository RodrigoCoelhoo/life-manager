package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalValueModel;

import java.io.Serializable;

public record NutritionalValueResponseDTO(
        Long id,
        NutritionalTag nutrient,
        Double per100units,
        String unit
) implements Serializable {
    public static NutritionalValueResponseDTO fromEntity(NutritionalValueModel model) {
        return new NutritionalValueResponseDTO(
                model.getId(),
                model.getTag(),
                model.getPer100units(),
                model.getTag().getUnit()
        );
    }
}
