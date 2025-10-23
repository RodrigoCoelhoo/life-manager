package com.rodrigocoelhoo.lifemanager.food.repository;

import com.rodrigocoelhoo.lifemanager.food.model.IngredientBrandModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientBrandRepository extends JpaRepository<IngredientBrandModel, Long> {

}
