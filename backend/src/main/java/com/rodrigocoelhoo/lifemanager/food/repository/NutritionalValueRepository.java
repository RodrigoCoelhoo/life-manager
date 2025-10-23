package com.rodrigocoelhoo.lifemanager.food.repository;

import com.rodrigocoelhoo.lifemanager.food.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.food.model.NutritionalValueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionalValueRepository extends JpaRepository<NutritionalValueModel, Long> {
    void deleteAllByIngredientBrand(IngredientBrandModel brand);
}
