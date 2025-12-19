package com.rodrigocoelhoo.lifemanager.nutrition.repository;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientModel, Long> {
    Page<IngredientModel> findAllByUser(UserModel user, Pageable pageable);
    Page<IngredientModel> findByUserAndNameContainingIgnoreCase(UserModel user, String name, Pageable pageable);
    Optional<IngredientModel> findByUserAndId(UserModel user, Long id);
    List<IngredientModel> findAllByUserAndIdIn(UserModel user, List<Long> ids);
}