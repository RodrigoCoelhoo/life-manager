package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDetailsDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseStats;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.*;
import com.rodrigocoelhoo.lifemanager.training.mapper.SessionExerciseMapper;
import com.rodrigocoelhoo.lifemanager.training.model.*;
import com.rodrigocoelhoo.lifemanager.training.repository.TrainingSessionRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final UserService userService;
    private final ExerciseService exerciseService;
    private final SessionExerciseMapper mapper;

    public TrainingSessionService(
            TrainingSessionRepository trainingSessionRepository,
            UserService userService,
            ExerciseService exerciseService,
            SessionExerciseMapper mapper
    ) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userService = userService;
        this.exerciseService = exerciseService;
        this.mapper = mapper;
    }

    public Page<TrainingSessionModel> getAllSessions(Pageable pageable) {
        UserModel user = userService.getLoggedInUser();
        return trainingSessionRepository.findAllByUser(user, pageable);
    }

    public TrainingSessionModel getSession(Long id) {
        UserModel user = userService.getLoggedInUser();
        return trainingSessionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFound("Session with ID '" + id + "' does not belong to the current user"));
    }

    public SessionDetailsDTO getSessionDetails(Long id) {
        TrainingSessionModel session = getSession(id);
        List<SessionExerciseModel> sessionExercises = session.getExercises();

        Map<ExerciseModel, List<SessionExerciseModel>> grouped = sessionExercises.stream()
                        .collect(Collectors.groupingBy(
                                SessionExerciseModel::getExercise,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        List<ExerciseDetailsDTO> exerciseDetails = grouped.keySet().stream()
                .map(exercise -> {
                    List<SessionExerciseBaseDTO> sets = grouped.getOrDefault(exercise, List.of())
                            .stream()
                            .map(mapper::toDTO)
                            .toList();
                    return ExerciseDetailsDTO.fromEntity(exercise, sets);
                })
                .toList();

        return SessionDetailsDTO.fromEntities(session, exerciseDetails);
    }

    public List<TrainingSessionModel> getSessionsByRange(
            LocalDateTime start,
            LocalDateTime end
    ) {
        UserModel user = userService.getLoggedInUser();
        return trainingSessionRepository.findAllByUserAndDateBetweenOrderByDateDescIdDesc(user, start, end);
    }

    @Transactional
    public TrainingSessionModel createSession(TrainingSessionDTO data) {
        UserModel user = userService.getLoggedInUser();

        TrainingSessionModel session = TrainingSessionModel.builder()
                .user(user)
                .exercises(new ArrayList<>())
                .build();

        applySessionData(session, data);
        return trainingSessionRepository.save(session);
    }


    @Transactional
    public TrainingSessionModel updateSession(
            Long id,
            TrainingSessionDTO data
    ) {
        TrainingSessionModel session = getSession(id);
        applySessionData(session, data);
        return trainingSessionRepository.save(session);
    }


    @Transactional
    public void deleteSession(Long id) {
        TrainingSessionModel session = getSession(id);
        trainingSessionRepository.delete(session);
    }

    private void applySessionData(TrainingSessionModel session, TrainingSessionDTO data) {
        session.setDate(data.date());

        List<SessionExerciseModel> newExercises = new ArrayList<>();

        for (SessionExerciseDTO ex : data.exercises()) {
            ExerciseModel exercise = exerciseService.getExercise(ex.exerciseId());

            List<SessionExerciseSetDTO> exerciseSets = ex.sets();

            if(exerciseSets.isEmpty()) {
                SessionExerciseModel sessionExercise = new SessionExerciseModel();
                sessionExercise.setSession(session);
                sessionExercise.setExercise(exercise);

                if(exercise.getType().toString().equals("SET_REP")) {
                    sessionExercise.setSetNumber(1);
                    sessionExercise.setWeight(0.0);
                    sessionExercise.setReps(0);
                } else {
                    sessionExercise.setDistance(0);
                    sessionExercise.setDurationSecs(0);
                }

                newExercises.add(sessionExercise);
            } else {
                List<SessionExerciseModel> sets = exerciseSets.stream().map(
                        set -> {
                            SessionExerciseSetDTO.validateForType(set, exercise.getType());

                            SessionExerciseModel sessionExercise = new SessionExerciseModel();
                            sessionExercise.setSession(session);
                            sessionExercise.setExercise(exercise);

                            applySessionExerciseData(sessionExercise, set, exercise.getType());
                            return sessionExercise;
                        }
                ).toList();

                newExercises.addAll(sets);
            }
        }

        session.getExercises().clear();
        session.getExercises().addAll(newExercises);
    }

    private void applySessionExerciseData(SessionExerciseModel sessionExercise, SessionExerciseSetDTO data, ExerciseType type) {
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
}
