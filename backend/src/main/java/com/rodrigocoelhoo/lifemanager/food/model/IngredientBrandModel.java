package com.rodrigocoelhoo.lifemanager.food.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_ingredientbrands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientBrandModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private IngredientModel ingredient;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "ingredientBrand", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NutritionalValueModel> nutritionalValues;

}
