package com.rodrigocoelhoo.lifemanager.nutrition.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id")
    private RecipeModel recipe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id")
    private IngredientModel ingredient;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Unit unit;
}

