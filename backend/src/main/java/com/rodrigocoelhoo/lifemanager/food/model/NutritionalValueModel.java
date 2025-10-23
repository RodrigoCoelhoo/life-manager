package com.rodrigocoelhoo.lifemanager.food.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_nutritionalvalues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
