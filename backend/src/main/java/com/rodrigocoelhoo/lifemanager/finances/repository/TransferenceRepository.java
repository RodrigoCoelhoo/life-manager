package com.rodrigocoelhoo.lifemanager.finances.repository;

import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransferenceRepository extends JpaRepository<TransferenceModel, Long> {
    Page<TransferenceModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<TransferenceModel> findByUserAndId(UserModel user, Long id);
    List<TransferenceModel> findTop5ByUserAndDateBetweenOrderByDateDescIdDesc(UserModel user, LocalDate start, LocalDate end);
}
