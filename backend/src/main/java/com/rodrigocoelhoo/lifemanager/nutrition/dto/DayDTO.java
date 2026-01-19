package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public record DayDTO(
        LocalDate date,
        List<NutritionalLabelEntrieDTO> tags
) implements Serializable {
    public static DayDTO fromEntity(LocalDate date, LinkedHashMap<NutritionalTag, Double> nutritionalLabel) {
        return new DayDTO(
                date,
                nutritionalLabel.entrySet().stream()
                        .map(entry -> NutritionalLabelEntrieDTO.fromEntities(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }
}
