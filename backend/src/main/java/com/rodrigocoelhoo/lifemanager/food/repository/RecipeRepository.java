package com.rodrigocoelhoo.lifemanager.food.repository;

import com.rodrigocoelhoo.lifemanager.food.model.RecipeModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeModel, Long> {
    List<RecipeModel> findAllByUser(UserModel user);
    Optional<RecipeModel> findByUserAndId(UserModel user, Long id);
}
