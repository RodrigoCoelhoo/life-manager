package com.rodrigocoelhoo.lifemanager.training.repository;

import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSessionModel, Long> {
    Page<TrainingSessionModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<TrainingSessionModel> findByIdAndUser(Long id, UserModel user);
}
