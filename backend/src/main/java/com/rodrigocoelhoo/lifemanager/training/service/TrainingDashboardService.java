package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.training.dto.MonthOverviewDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseType;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class TrainingDashboardService {

    private final TrainingSessionService trainingSessionService;

    public TrainingDashboardService(
            TrainingSessionService trainingSessionService
    ) {
        this.trainingSessionService = trainingSessionService;
    }

    private static final String CACHE_DASHBOARD = "financesDashboard";

    @Cacheable(
            value = CACHE_DASHBOARD,
            key = "T(com.rodrigocoelhoo.lifemanager.config.RedisCacheService).getCurrentUsername() + " +
                    "'::month:' + #date"
    )
    public MonthOverviewDTO getMonthOverview(YearMonth date) {
        LocalDateTime start = date.atDay(1).atTime(0, 0, 0);
        LocalDateTime end   = date.atEndOfMonth().atTime(23, 59, 59);

        List<TrainingSessionModel> sessions = trainingSessionService.getSessionsByRange(start, end);

        List<LocalDate> dates = new ArrayList<>();
        double volume = 0.0;
        int timeSecs = 0;
        double distance = 0.0;
        HashMap<Long, ExercisePR> exercisePr = new HashMap<>();

        for(TrainingSessionModel session : sessions) {
            dates.add(LocalDate.from(session.getDate()));

            List<SessionExerciseModel> exerciseSets = session.getExercises();
            for(SessionExerciseModel set : exerciseSets) {
                ExerciseModel exercise = set.getExercise();

                if(exercise.getType().equals(ExerciseType.SET_REP)) {
                    ExercisePR pr = exercisePr.get(exercise.getId());

                    double setVolume = set.getWeight() * set.getReps();
                    volume += setVolume;
                    double setE1RM = set.getWeight() * (1 + set.getReps() / 30.0);

                    if (pr == null) {
                        exercisePr.put(
                                exercise.getId(),
                                new ExercisePR(
                                        exercise.getName(),
                                        set.getWeight(),
                                        new ExercisePR.RepSet(set.getReps(), set.getWeight()),
                                        setE1RM
                                )
                        );
                    } else {
                        double maxWeight = Math.max(pr.maxWeight(), set.getWeight());

                        double bestVolume = pr.bestRepSet().reps() * pr.bestRepSet().weight();
                        ExercisePR.RepSet bestRepSet =
                                setVolume > bestVolume ?
                                        new ExercisePR.RepSet(set.getReps(), set.getWeight()) :
                                        pr.bestRepSet();

                        double bestE1RM = Math.max(pr.bestE1RM(), setE1RM);

                        exercisePr.put(
                                exercise.getId(),
                                new ExercisePR(exercise.getName(), maxWeight, bestRepSet, bestE1RM)
                        );
                    }

                } else {
                    timeSecs += set.getDurationSecs();
                    distance += set.getDistance();
                }
            }
        }

        return MonthOverviewDTO.fromEntities(
                dates,
                volume,
                timeSecs,
                distance,
                exercisePr
        );
    }

    public record ExercisePR(
            String name,
            double maxWeight,
            RepSet bestRepSet,
            double bestE1RM
    ) implements Serializable {
        public record RepSet(int reps, double weight) implements Serializable {}
    }

}
