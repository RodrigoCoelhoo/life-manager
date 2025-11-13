package com.rodrigocoelhoo.lifemanager.nutrition.model;

import lombok.Getter;

@Getter
public enum NutritionalTag {
    CALORIES("kcal"),

    PROTEIN("g"),
    CARBOHYDRATES("g"),
    SUGARS("g"),
    FIBER("g"),
    FAT("g"),
    SATURATED_FAT("g"),
    TRANS_FAT("g"),
    OMEGA_3("g"),
    CHOLESTEROL("mg"),

    SODIUM("mg"),
    POTASSIUM("mg"),
    CALCIUM("mg"),
    IRON("mg"),

    VITAMIN_A("µg"),
    VITAMIN_C("mg"),
    VITAMIN_D("µg"),
    VITAMIN_E("mg"),
    VITAMIN_K("µg"),
    VITAMIN_B12("µg");

    private final String unit;

    NutritionalTag(String unit) {
        this.unit = unit;
    }
}
