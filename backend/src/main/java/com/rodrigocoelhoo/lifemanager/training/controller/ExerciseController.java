package com.rodrigocoelhoo.lifemanager.training.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransactionResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseStats;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.service.ExerciseService;
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
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(
            ExerciseService exerciseService
    ) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<ExerciseResponseDTO>> getExercises(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        return ResponseEntity.ok(PageResponseDTO.fromPage(exerciseService.getAllExercisesByUser(pageable, name)));
    }

    @GetMapping("/{exercise_id}")
    public ResponseEntity<ExerciseResponseDTO> getExercise(
            @PathVariable Long exercise_id
    ) {
        ExerciseModel exercise = exerciseService.getExercise(exercise_id);
        return ResponseEntity.ok(ExerciseResponseDTO.fromEntity(exercise));
    }

    @GetMapping("/{exercise_id}/stats")
    public ResponseEntity<ExerciseStats> getExerciseStats(
            @PathVariable Long exercise_id
    ) {
        ExerciseStats stats = exerciseService.getExerciseStats(exercise_id);
        return ResponseEntity.ok(stats);
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
