package com.rodrigocoelhoo.lifemanager.finances.repository;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutomaticTransactionRepository extends JpaRepository<AutomaticTransactionModel, Long> {
    Page<AutomaticTransactionModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<AutomaticTransactionModel> findByUserAndId(UserModel user, Long id);
    List<AutomaticTransactionModel> findByNextTransactionDate(LocalDate nextTransactionDate);
}
