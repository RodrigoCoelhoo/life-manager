package com.rodrigocoelhoo.lifemanager.training.repository;

import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionExerciseRepository extends JpaRepository<SessionExerciseModel, Long> {
    List<SessionExerciseModel> findAllBySessionId_Id(Long sessionId);
    Optional<SessionExerciseModel> findByIdAndSessionIdAndExercise(Long id, TrainingSessionModel session, ExerciseModel exercise);
}