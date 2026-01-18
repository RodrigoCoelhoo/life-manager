package com.rodrigocoelhoo.lifemanager.training.repository;

import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSessionModel, Long> {
    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    Page<TrainingSessionModel> findAllByUser(UserModel user, Pageable pageable);
    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    Optional<TrainingSessionModel> findByIdAndUser(Long id, UserModel user);
    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    List<TrainingSessionModel> findAllByUserAndDateBetweenOrderByDateDescIdDesc(UserModel user, LocalDateTime start, LocalDateTime end);
}
