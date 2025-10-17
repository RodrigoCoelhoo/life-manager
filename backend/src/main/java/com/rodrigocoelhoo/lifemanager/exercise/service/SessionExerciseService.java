package com.rodrigocoelhoo.lifemanager.exercise.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.exercise.dto.sessionexercisedto.SessionExerciseDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.exercise.model.ExerciseType;
import com.rodrigocoelhoo.lifemanager.exercise.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.exercise.repository.SessionExerciseRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionExerciseService {

    private final SessionExerciseRepository sessionExerciseRepository;
    private final TrainingSessionService trainingSessionService;

    public SessionExerciseService(
            SessionExerciseRepository sessionExerciseRepository,
            TrainingSessionService trainingSessionService

    ) {
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.trainingSessionService = trainingSessionService;
    }

    public List<SessionExerciseModel> getAllSessionExercises(Long id) {
        return sessionExerciseRepository.findAllBySessionId_Id(id);
    }

    public SessionExerciseModel createExerciseDetails(Long session_id, Long exercise_id, SessionExerciseDTO data) {
        TrainingSessionModel session = trainingSessionService.getSession(session_id);
        ExerciseModel exercise = session.getTrainingPlan().getExercises().stream()
                .filter(e -> e.getId().equals(exercise_id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound(
                        "Exercise with ID '" + exercise_id + "' doesn't belong to this training plan"
                ));

        SessionExerciseDTO.validateForType(data, exercise.getType());

        SessionExerciseModel sessionExercise = new SessionExerciseModel();
        sessionExercise.setSessionId(session);
        sessionExercise.setExercise(exercise);
        applySessionExerciseData(sessionExercise, data, exercise.getType());
        return sessionExerciseRepository.save(sessionExercise);
    }

    public SessionExerciseModel updateExerciseDetails(Long session_id, Long exercise_id, Long sessionSet_id ,@Valid SessionExerciseDTO data) {
        TrainingSessionModel session = trainingSessionService.getSession(session_id);
        ExerciseModel exercise = session.getTrainingPlan().getExercises().stream()
                .filter(e -> e.getId().equals(exercise_id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound(
                        "Exercise with ID '" + exercise_id + "' doesn't belong to the training plan with ID '" + session.getTrainingPlan().getId() + "'"
                ));

        SessionExerciseDTO.validateForType(data, exercise.getType());

        SessionExerciseModel sessionExercise = sessionExerciseRepository.findByIdAndSessionIdAndExercise(sessionSet_id, session, exercise)
                .orElseThrow(
                        () -> new ResourceNotFound("Session set with ID '" + sessionSet_id + "' doesn't belong to session with ID '" + session_id + "'")
                );

        applySessionExerciseData(sessionExercise, data, exercise.getType());
        return sessionExerciseRepository.save(sessionExercise);
    }

    private void applySessionExerciseData(SessionExerciseModel sessionExercise, SessionExerciseDTO data, ExerciseType type) {
        if(type == ExerciseType.SET_REP) {
            sessionExercise.setSetNumber(data.setNumber());
            sessionExercise.setReps(data.reps());
            sessionExercise.setWeight(data.weight());
            sessionExercise.setDurationSecs(null);
            sessionExercise.setDistance(null);
        } else {
            sessionExercise.setSetNumber(null);
            sessionExercise.setReps(null);
            sessionExercise.setWeight(null);
            sessionExercise.setDurationSecs(data.durationSecs());
            sessionExercise.setDistance(data.distance());
        }
    }

    public void deleteExerciseDetails(Long session_id, Long exercise_id, Long sessionSet_id) {
        TrainingSessionModel session = trainingSessionService.getSession(session_id);

        ExerciseModel exercise = session.getTrainingPlan().getExercises().stream()
                .filter(e -> e.getId().equals(exercise_id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound(
                        "Exercise with ID '" + exercise_id + "' doesn't belong to the training plan with ID '" + session.getTrainingPlan().getId() + "'"
                ));

        SessionExerciseModel sessionExercise = sessionExerciseRepository.findByIdAndSessionIdAndExercise(sessionSet_id, session, exercise)
                .orElseThrow(
                        () -> new ResourceNotFound("Session set with ID '" + sessionSet_id + "' doesn't belong to session with ID '" + session_id + "'")
                );

        sessionExerciseRepository.delete(sessionExercise);
    }
}
