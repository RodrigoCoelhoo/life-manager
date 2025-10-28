package com.rodrigocoelhoo.lifemanager.finances.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransferenceDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransferenceResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.finances.service.TransferenceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferences")
public class TransferenceController {

    private final TransferenceService transferenceService;

    public TransferenceController(
            TransferenceService transferenceService
    ) {
        this.transferenceService = transferenceService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<TransferenceResponseDTO>> getAllTransferences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Page<TransferenceModel> transferences = transferenceService.getAllTransferences(pageable);
        Page<TransferenceResponseDTO> response = transferences.map(TransferenceResponseDTO::fromEntity);

        return ResponseEntity.ok(PageResponseDTO.fromPage(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenceResponseDTO> getTransference(
            @PathVariable Long id
    ) {
        TransferenceModel transference = transferenceService.getTransference(id);
        return ResponseEntity.ok(TransferenceResponseDTO.fromEntity(transference));
    }

    @PostMapping
    public ResponseEntity<TransferenceResponseDTO> createTransference(
            @RequestBody @Valid TransferenceDTO data
    ) {
        TransferenceModel transference = transferenceService.createTransference(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransferenceResponseDTO.fromEntity(transference));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransferenceResponseDTO> updateTransference(
            @PathVariable Long id,
            @RequestBody @Valid TransferenceDTO data
    ) {
        TransferenceModel transference = transferenceService.updateTransference(id, data);
        return ResponseEntity.ok(TransferenceResponseDTO.fromEntity(transference));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransference(
            @PathVariable Long id
    ) {
        transferenceService.deleteTransference(id);
        return ResponseEntity.noContent().build();
    }
}
