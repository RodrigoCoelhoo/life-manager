package com.rodrigocoelhoo.lifemanager.training.controller;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(
            ExerciseService exerciseService
    ) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDTO>> getExercises() {
        List<ExerciseModel> exercises = exerciseService.getAllExercisesByUser();

        List<ExerciseResponseDTO> response = exercises.stream()
                .map(ExerciseResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{exercise_id}")
    public ResponseEntity<ExerciseResponseDTO> getExercise(
            @PathVariable Long exercise_id
    ) {
        ExerciseModel exercise = exerciseService.getExercise(exercise_id);
        return ResponseEntity.ok(ExerciseResponseDTO.fromEntity(exercise));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDTO> createExercise(
            @RequestBody @Valid ExerciseDTO data
    ) {
        ExerciseModel exercise = exerciseService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciseResponseDTO.fromEntity(exercise));
    }

    @PutMapping("/{exercise_id}")
    public ResponseEntity<ExerciseResponseDTO> updateExercise(
            @PathVariable Long exercise_id,
            @RequestBody @Valid ExerciseUpdateDTO data
    ) {
        ExerciseModel exercise = exerciseService.update(exercise_id, data);
        return ResponseEntity.ok(ExerciseResponseDTO.fromEntity(exercise));
    }

    @DeleteMapping("/{exercise_id}")
    public ResponseEntity<?> deleteExercise(
            @PathVariable Long exercise_id
    ) {
        exerciseService.delete(exercise_id);
        return ResponseEntity.noContent().build();
    }
}
