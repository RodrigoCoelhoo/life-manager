package com.rodrigocoelhoo.lifemanager.training.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.training.service.TrainingPlanService;
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
@RequestMapping("/api/training-plans")
public class TrainingPlanController {


    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(
            TrainingPlanService trainingPlanService
    ) {
        this.trainingPlanService = trainingPlanService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<TrainingPlanResponseDTO>> getTrainingPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        Page<TrainingPlanModel> plans = trainingPlanService.getAllTrainingPlansByUser(pageable);

        Page<TrainingPlanResponseDTO> response = plans.map(TrainingPlanResponseDTO::fromEntity);

        return ResponseEntity.ok(PageResponseDTO.fromPage(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanDetailsDTO> getTrainingPlan(
            @PathVariable Long id
    ) {
        TrainingPlanModel plan = trainingPlanService.getTrainingPlan(id);
        return ResponseEntity.ok(TrainingPlanDetailsDTO.fromEntity(plan));
    }

    @PostMapping
    public ResponseEntity<TrainingPlanResponseDTO> createTrainingPlan(
            @RequestBody @Valid TrainingPlanDTO data
    ) {
        TrainingPlanModel plan = trainingPlanService.createTrainingPlan(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TrainingPlanResponseDTO.fromEntity(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingPlanDetailsDTO> updateTrainingPlan(
            @PathVariable Long id,
            @RequestBody @Valid TrainingPlanUpdateDTO data
    ) {
        TrainingPlanModel plan = trainingPlanService.updateTrainingPlan(id, data);
        return ResponseEntity.ok(TrainingPlanDetailsDTO.fromEntity(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainingPlan(
            @PathVariable Long id
    ) {
        trainingPlanService.deleteTrainingPlan(id);
        return ResponseEntity.noContent().build();
    }
}
