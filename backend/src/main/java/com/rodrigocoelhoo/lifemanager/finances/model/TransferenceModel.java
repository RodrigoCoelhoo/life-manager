package com.rodrigocoelhoo.lifemanager.finances.model;

import com.rodrigocoelhoo.lifemanager.users.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_transferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_wallet_id", nullable = false)
    private WalletModel fromWallet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_wallet_id", nullable = false)
    private WalletModel toWallet;

    @Positive
    @Column(precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 512, nullable = false)
    private String description;
}
