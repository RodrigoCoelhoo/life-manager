package com.rodrigocoelhoo.lifemanager.exercise.controller;

import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanDTO;
import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanDetailsDTO;
import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanUpdateDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.exercise.service.TrainingPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-plan")
public class TrainingPlanController {


    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(
            TrainingPlanService trainingPlanService
    ) {
        this.trainingPlanService = trainingPlanService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingPlanResponseDTO>> getTrainingPlans() {
        List<TrainingPlanModel> plans = trainingPlanService.getAllTrainingPlansByUser();

        List<TrainingPlanResponseDTO> response = plans.stream()
                .map(TrainingPlanResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanDetailsDTO> getTrainingPlan(@PathVariable Long id) {
        TrainingPlanModel plan = trainingPlanService.getTrainingPlan(id);
        return ResponseEntity.ok(TrainingPlanDetailsDTO.fromEntity(plan));
    }

    @PostMapping
    public ResponseEntity<TrainingPlanResponseDTO> createTrainingPlan(@RequestBody @Valid TrainingPlanDTO data) {
        TrainingPlanModel plan = trainingPlanService.createTrainingPlan(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TrainingPlanResponseDTO.fromEntity(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingPlanDetailsDTO> updateTrainingPlan(@PathVariable Long id, @RequestBody @Valid TrainingPlanUpdateDTO data) {
        TrainingPlanModel plan = trainingPlanService.updateTrainingPlan(id, data);
        return ResponseEntity.ok(TrainingPlanDetailsDTO.fromEntity(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainingPlan(@PathVariable Long id) {
        trainingPlanService.deleteTrainingPlan(id);
        return ResponseEntity.noContent().build();
    }
}
