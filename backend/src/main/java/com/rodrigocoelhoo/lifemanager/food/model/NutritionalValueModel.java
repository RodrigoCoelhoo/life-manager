package com.rodrigocoelhoo.lifemanager.food.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_nutritionalvalues")
public class NutritionalValueModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_brand_id", nullable = false)
    private IngredientBrandModel ingredientBrand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NutritionalTag tag;

    @Column(nullable = false)
    private Double per100units;
}
