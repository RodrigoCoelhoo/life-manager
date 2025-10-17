package com.rodrigocoelhoo.lifemanager.food.model;

import lombok.Getter;

@Getter
public enum NutritionalTag {
    PROTEIN("g"),
    CALORIES("kcal"),
    FAT("g"),
    CARBOHYDRATES("g");

    private final String unit;

    NutritionalTag(String unit) {
        this.unit = unit;
    }
}
