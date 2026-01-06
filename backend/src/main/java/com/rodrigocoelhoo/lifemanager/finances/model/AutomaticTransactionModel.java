package com.rodrigocoelhoo.lifemanager.finances.model;

import com.rodrigocoelhoo.lifemanager.users.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_automatic_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutomaticTransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id")
    private WalletModel wallet;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String name;

    @Positive
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionRecurrence recurrence; // Daily, Weekly, Monthly

    @Positive
    @Column(nullable = false, name = "recurrence_interval")
    private short interval; // interval {recurrency}, Ex.: 2 Weekly -> every 2 weeks

    @Column(length = 512, nullable = false)
    private String description;

    @Column(nullable = false)
    LocalDate nextTransactionDate;
}
