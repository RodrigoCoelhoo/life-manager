package com.rodrigocoelhoo.lifemanager.training.controller;

import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.TrainingSessionDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.TrainingSessionDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.TrainingSessionResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.training.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {


    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(
            TrainingSessionService trainingSessionService
    ) {
        this.trainingSessionService = trainingSessionService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingSessionResponseDTO>> getSessions() {
        List<TrainingSessionModel> sessions = trainingSessionService.getAllSessions();

        List<TrainingSessionResponseDTO> response = sessions.stream()
                .map(TrainingSessionResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSessionDetailsDTO> getSession(
            @PathVariable Long id
    ) {
        TrainingSessionModel session = trainingSessionService.getSession(id);
        return ResponseEntity.ok(TrainingSessionDetailsDTO.fromEntity(session));
    }

    @PostMapping
    public ResponseEntity<TrainingSessionResponseDTO> createSession(
            @RequestBody @Valid TrainingSessionDTO data
    ) {
        TrainingSessionModel session = trainingSessionService.createSession(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TrainingSessionResponseDTO.fromEntity(session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingSessionResponseDTO> updateSession(
            @PathVariable Long id,
            @RequestBody @Valid TrainingSessionDTO data
    ) {
        TrainingSessionModel session = trainingSessionService.updateSession(id, data);
        return ResponseEntity.ok(TrainingSessionResponseDTO.fromEntity(session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(
            @PathVariable Long id
    ) {
        trainingSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
