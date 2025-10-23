package com.rodrigocoelhoo.lifemanager.food.repository;

import com.rodrigocoelhoo.lifemanager.food.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientModel, Long> {
    List<IngredientModel> findAllByUser(UserModel user);
    Optional<IngredientModel> findByUserAndId(UserModel user, Long id);
    List<IngredientModel> findAllByUserAndIdIn(UserModel user, List<Long> ids);
}