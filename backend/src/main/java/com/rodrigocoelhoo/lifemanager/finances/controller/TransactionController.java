package com.rodrigocoelhoo.lifemanager.finances.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransactionDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransactionResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,

            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) ExpenseCategory category,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("date").descending().and(Sort.by("id").descending())
        );

        Page<TransactionResponseDTO> response = transactionService.getAllTransactions(
                pageable,
                walletId,
                category,
                startDate,
                endDate
        );

        return ResponseEntity.ok(PageResponseDTO.fromPage(response));
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody @Valid TransactionDTO data
    ) {
        TransactionModel transaction = transactionService.createTransaction(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponseDTO.fromEntity(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody @Valid TransactionDTO data
    ) {
        TransactionModel transaction = transactionService.updateTransaction(id, data);
        return ResponseEntity.ok(TransactionResponseDTO.fromEntity(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id
    ) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
