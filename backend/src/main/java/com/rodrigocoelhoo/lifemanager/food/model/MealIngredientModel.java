package com.rodrigocoelhoo.lifemanager.food.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_meal_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealIngredientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private MealModel meal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id")
    private IngredientModel ingredient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id")
    private IngredientBrandModel brand;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Unit unit;
}

