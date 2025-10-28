package com.rodrigocoelhoo.lifemanager.finances.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.AutomaticTransactionDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.AutomaticTransactionResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.service.AutomaticTransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/automatic-transactions")
public class AutomaticTransactionController {

    private final AutomaticTransactionService automaticTransactionService;

    public AutomaticTransactionController(
            AutomaticTransactionService automaticTransactionService
    ) {
        this.automaticTransactionService = automaticTransactionService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<AutomaticTransactionResponseDTO>> getAllAutomaticTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nextTransactionDate").descending());

        Page<AutomaticTransactionModel> transactions = automaticTransactionService.getAllAutomaticTransactions(pageable);
        Page<AutomaticTransactionResponseDTO> response = transactions.map(AutomaticTransactionResponseDTO::fromEntity);

        return ResponseEntity.ok(PageResponseDTO.fromPage(response));
    }

    @PostMapping
    public ResponseEntity<AutomaticTransactionResponseDTO> createAutomaticTransaction(
            @RequestBody @Valid AutomaticTransactionDTO data
    ) {
        AutomaticTransactionModel automaticTransaction = automaticTransactionService.createAutomaticTransaction(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(AutomaticTransactionResponseDTO.fromEntity(automaticTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutomaticTransactionResponseDTO> updateAutomaticTransaction(
            @PathVariable Long id,
            @RequestBody @Valid AutomaticTransactionDTO data
    ) {
        AutomaticTransactionModel automaticTransaction = automaticTransactionService.updateAutomaticTransaction(id, data);
        return ResponseEntity.ok(AutomaticTransactionResponseDTO.fromEntity(automaticTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutomaticTransactions(
            @PathVariable Long id
    ) {
        automaticTransactionService.deleteAutomaticTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
