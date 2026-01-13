package com.rodrigocoelhoo.lifemanager.finances.repository;

import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionModel, Long>, JpaSpecificationExecutor<TransactionModel> {
    Page<TransactionModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<TransactionModel> findByUserAndId(UserModel user, Long id);
    List<TransactionModel> findAllByUserAndDateBetweenOrderByDateDescIdDesc(UserModel user, LocalDate start, LocalDate end);
}
