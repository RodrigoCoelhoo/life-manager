package com.rodrigocoelhoo.lifemanager.finances.repository;

import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletModel, Long> {
    List<WalletModel> findAllByUser(UserModel user);
    Optional<WalletModel> findByUserAndId(UserModel user, Long id);
}
