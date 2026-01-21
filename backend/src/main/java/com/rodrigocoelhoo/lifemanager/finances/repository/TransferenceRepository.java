package com.rodrigocoelhoo.lifemanager.finances.repository;

import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransferenceRepository extends JpaRepository<TransferenceModel, Long>, JpaSpecificationExecutor<TransferenceModel> {
    Page<TransferenceModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<TransferenceModel> findByUserAndId(UserModel user, Long id);
    @EntityGraph(attributePaths = {"fromWallet", "toWallet"})
    List<TransferenceModel> findTop5ByUserAndDateBetweenOrderByDateDescIdDesc(UserModel user, LocalDate start, LocalDate end);

    @EntityGraph(attributePaths = {"fromWallet", "toWallet"})
    @Override
    Page<TransferenceModel> findAll(
            @Nullable Specification<TransferenceModel> spec,
            @NonNull Pageable pageable
    );
}
