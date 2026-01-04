package com.rodrigocoelhoo.lifemanager.nutrition.repository;

import com.rodrigocoelhoo.lifemanager.nutrition.model.MealModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<MealModel, Long> {
    Page<MealModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<MealModel> findByUserAndId(UserModel user, Long id);
    List<MealModel> findAllByUserAndDateBetweenOrderByDateDescIdDesc(UserModel user, LocalDateTime start, LocalDateTime end);
}