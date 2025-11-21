package com.rodrigocoelhoo.lifemanager.training.repository;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseModel, Long> {
    Page<ExerciseModel> findAllByUser(UserModel user, Pageable pageable);
    Page<ExerciseModel> findByUserAndNameContainingIgnoreCase(UserModel user, String name, Pageable pageable);
    List<ExerciseModel> findAllByIdInAndUser(List<Long> ids, UserModel user);
    Optional<ExerciseModel> findByIdAndUser(Long id, UserModel user);
}
