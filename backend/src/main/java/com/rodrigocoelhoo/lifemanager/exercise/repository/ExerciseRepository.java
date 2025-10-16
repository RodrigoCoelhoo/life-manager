package com.rodrigocoelhoo.lifemanager.exercise.repository;

import com.rodrigocoelhoo.lifemanager.exercise.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseModel, Long> {
    List<ExerciseModel> findAllByUser(UserModel user);
    List<ExerciseModel> findAllByIdInAndUser(List<Long> ids, UserModel user);
    Optional<ExerciseModel> findByIdAndUser(Long id, UserModel user);
}
