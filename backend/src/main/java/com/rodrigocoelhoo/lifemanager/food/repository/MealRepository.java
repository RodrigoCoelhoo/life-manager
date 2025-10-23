package com.rodrigocoelhoo.lifemanager.food.repository;

import com.rodrigocoelhoo.lifemanager.food.model.MealModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<MealModel, Long> {
    List<MealModel> findAllByUser(UserModel user);
    Optional<MealModel> findByUserAndId(UserModel user, Long id);
}