package com.rodrigocoelhoo.lifemanager.training.repository;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlanModel, Long> {
    Page<TrainingPlanModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<TrainingPlanModel> findByIdAndUser(Long id, UserModel user);
    List<TrainingPlanModel> findAllByExercisesContains(ExerciseModel exercise);
}