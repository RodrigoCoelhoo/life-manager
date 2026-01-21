package com.rodrigocoelhoo.lifemanager.finances.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletUpdateDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(
            WalletService walletService
    ) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<WalletResponseDTO>> getWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(PageResponseDTO.fromPage(walletService.getWallets(pageable, name)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDTO> getWallet(
            @PathVariable Long id
    ) {
        WalletModel wallet = walletService.getWallet(id);
        return ResponseEntity.ok(WalletResponseDTO.fromEntity(wallet));
    }

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(
            @RequestBody @Valid WalletDTO data
    ) {
        WalletModel wallet = walletService.createWallet(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletResponseDTO.fromEntity(wallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponseDTO> updateWallet(
            @PathVariable Long id,
            @RequestBody @Valid WalletUpdateDTO data
    ) {
        WalletModel wallet = walletService.updateWallet(id, data);
        return ResponseEntity.ok(WalletResponseDTO.fromEntity(wallet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(
            @PathVariable Long id
    ) {
        walletService.deleteWallet(id);
        return ResponseEntity.noContent().build();
    }
}
