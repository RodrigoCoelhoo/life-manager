package com.rodrigocoelhoo.lifemanager.finances.repository;

import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletModel, Long> {
    Page<WalletModel> findAllByUser(UserModel user, Pageable pageable);
    Optional<WalletModel> findByUserAndId(UserModel user, Long id);
    Page<WalletModel> findByUserAndNameContainingIgnoreCase(UserModel user, String name, Pageable pageable);
}
