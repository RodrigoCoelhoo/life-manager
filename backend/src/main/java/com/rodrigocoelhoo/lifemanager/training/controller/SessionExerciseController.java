package com.rodrigocoelhoo.lifemanager.training.controller;

import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionExerciseBaseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionExerciseDTO;
import com.rodrigocoelhoo.lifemanager.training.mapper.SessionExerciseMapper;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.training.service.SessionExerciseService;
import com.rodrigocoelhoo.lifemanager.training.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/training-sessions")
public class SessionExerciseController {

    private final TrainingSessionService trainingSessionService;
    private final SessionExerciseService sessionExerciseService;
    private final SessionExerciseMapper mapper;

    public SessionExerciseController(
            TrainingSessionService trainingSessionService,
            SessionExerciseService sessionExerciseService,
            SessionExerciseMapper mapper
    ) {
        this.trainingSessionService = trainingSessionService;
        this.sessionExerciseService = sessionExerciseService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<SessionDetailsDTO> getSessionDetails(
            @PathVariable Long id
    ) {
        TrainingSessionModel session = trainingSessionService.getSession(id);
        List<SessionExerciseModel> sessionExercises = sessionExerciseService.getAllSessionExercises(session.getId());

        Map<ExerciseModel, List<SessionExerciseModel>> grouped = sessionExercises.stream()
                .collect(Collectors.groupingBy(SessionExerciseModel::getExercise));

        List<ExerciseDetailsDTO> exerciseDetails = session.getTrainingPlan().getExercises().stream()
                .map(exercise -> {
                    List<SessionExerciseBaseDTO> sets = grouped.getOrDefault(exercise, List.of())
                            .stream()
                            .map(mapper::toDTO)
                            .toList();
                    return ExerciseDetailsDTO.fromEntity(exercise, sets);
                })
                .toList();

        return ResponseEntity.ok(SessionDetailsDTO.fromEntities(session, exerciseDetails));
    }


    @PostMapping("/{session_id}/exercises/{exercise_id}")
    public ResponseEntity<SessionExerciseBaseDTO> createSessionExerciseDetails(
            @PathVariable Long session_id,
            @PathVariable Long exercise_id,
            @RequestBody @Valid SessionExerciseDTO data
    ) {
        SessionExerciseModel model = sessionExerciseService.createExerciseDetails(session_id, exercise_id, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(model));
    }

    @PutMapping("/{session_id}/exercises/{exercise_id}/sets/{sessionSet_id}")
    public ResponseEntity<SessionExerciseBaseDTO> updateSessionExerciseDetails(
            @PathVariable Long session_id,
            @PathVariable Long exercise_id,
            @PathVariable Long sessionSet_id,
            @RequestBody @Valid SessionExerciseDTO data
    ) {
        SessionExerciseModel model = sessionExerciseService.updateExerciseDetails(session_id, exercise_id, sessionSet_id, data);
        return ResponseEntity.ok(mapper.toDTO(model));
    }

    @DeleteMapping("/{session_id}/exercises/{exercise_id}/sets/{sessionSet_id}")
    public ResponseEntity<?> deleteSessionExerciseDetails(
            @PathVariable Long session_id,
            @PathVariable Long exercise_id,
            @PathVariable Long sessionSet_id
    ) {
        sessionExerciseService.deleteExerciseDetails(session_id, exercise_id, sessionSet_id);
        return ResponseEntity.noContent().build();
    }
}
