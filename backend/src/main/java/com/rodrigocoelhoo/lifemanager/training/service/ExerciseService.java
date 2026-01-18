package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.config.CachedPage;
import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseStats;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseType;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.repository.ExerciseRepository;
import com.rodrigocoelhoo.lifemanager.training.repository.SessionExerciseRepository;
import com.rodrigocoelhoo.lifemanager.training.repository.TrainingPlanRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExerciseService {

    private final UserService userService;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "exercises";
    private static final String CACHE_STATS = "exerciseStats";

    public ExerciseService(
            ExerciseRepository exerciseRepository,
            UserService userService,
            TrainingPlanRepository trainingPlanRepository,
            SessionExerciseRepository sessionExerciseRepository,
            RedisCacheService redisCacheService
    ) {
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
        this.trainingPlanRepository = trainingPlanRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.redisCacheService = redisCacheService;
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<ExerciseResponseDTO> getAllExercisesByUser(Pageable pageable, String name) {
        UserModel user = userService.getLoggedInUser();
        Page<ExerciseModel> page;
        if(name == null || name.isBlank())
            page = exerciseRepository.findAllByUser(user, pageable);
        else
            page = exerciseRepository.findByUserAndNameContainingIgnoreCase(user, name, pageable);

        return page.map(ExerciseResponseDTO::fromEntity);
    }

    public List<ExerciseModel> getExercisesForUser(List<Long> ids) {
        UserModel user = userService.getLoggedInUser();
        List<ExerciseModel> exercises = exerciseRepository.findAllByIdInAndUser(ids, user);

        if(exercises.size() != ids.size())
            throw new ResourceNotFound("Some exercises do not belong to the current user");

        return exercises;
    }

    public ExerciseModel getExercise(Long exercise_id) {
        UserModel user = userService.getLoggedInUser();
        return exerciseRepository.findByIdAndUser(exercise_id, user)
                .orElseThrow(() -> new ResourceNotFound("Exercise with ID '" + exercise_id + "' does not belong to the current user"));
    }


    @Transactional
    public ExerciseModel create(ExerciseDTO data) {
        UserModel user = userService.getLoggedInUser();
        ExerciseType type = ExerciseType.valueOf(data.type().toUpperCase());

        ExerciseModel exercise = ExerciseModel.builder()
                .user(user)
                .name(data.name())
                .description(data.description())
                .type(type)
                .demoUrl(data.demoUrl())
                .build();

        ExerciseModel saved = exerciseRepository.save(exercise);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public ExerciseModel update(Long exerciseId, ExerciseUpdateDTO data) {
        ExerciseModel exercise = getExercise(exerciseId);

        exercise.setName(data.name());
        exercise.setDescription(data.description());
        exercise.setDemoUrl(data.demoUrl());

        ExerciseModel saved = exerciseRepository.save(exercise);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public void delete(Long exerciseId) {
        ExerciseModel exercise = getExercise(exerciseId);

        trainingPlanRepository.findAllByExercisesContains(exercise)
                .forEach(plan -> plan.getExercises().remove(exercise));

        exerciseRepository.delete(exercise);
        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCache("trainingPlans");
        redisCacheService.evictUserCache("trainingSessions");
        redisCacheService.evictUserCache("trainingSession");
        redisCacheService.evictUserCache("trainingDashboard");
        redisCacheService.evictUserCacheSpecific(CACHE_STATS, "exercise:" + exerciseId);
    }

    @Cacheable(
            value = CACHE_STATS,
            key = "T(com.rodrigocoelhoo.lifemanager.config.RedisCacheService).getCurrentUsername() + " +
                    "'::exercise:' + #exerciseId"
    )
    public ExerciseStats getExerciseStats(Long exerciseId) {
        ExerciseModel exercise = getExercise(exerciseId);
        if(exercise.getType().equals(ExerciseType.TIME)) {
            return null;
        }

        List<SessionExerciseModel> sessions = sessionExerciseRepository.findAllByExercise(exercise);

        double volume = 0.0;
        int reps = 0;
        int sets = sessions.size();
        double maxWeight = 0.0;
        TrainingDashboardService.ExercisePR.RepSet bestRepSet = new TrainingDashboardService.ExercisePR.RepSet(0,0.0);
        double e1rm = 0.0;

        YearMonth cutoff = YearMonth.now().minusMonths(5);
        YearMonth current = YearMonth.now();

        Map<YearMonth, Double> monthlyMaxE1RM = new LinkedHashMap<>();

        while (!cutoff.isAfter(current)) {
            monthlyMaxE1RM.put(cutoff, 0.0);
            cutoff = cutoff.plusMonths(1);
        }

        for(SessionExerciseModel session : sessions) {
            reps += session.getReps();
            volume += session.getWeight() * session.getReps();
            maxWeight = Math.max(maxWeight, session.getWeight());
            double setE1RM = session.getWeight() * (1 + session.getReps() / 30.0);
            e1rm = Math.max(e1rm, setE1RM);

            double currentBestRepSet = bestRepSet.reps() * bestRepSet.weight();
            double sessionRepSet = session.getReps() * session.getWeight();
            bestRepSet = currentBestRepSet > sessionRepSet ? bestRepSet : new TrainingDashboardService.ExercisePR.RepSet(session.getReps(), session.getWeight());

            YearMonth setYearMonth = YearMonth.from(session.getSession().getDate());
            if (!monthlyMaxE1RM.containsKey(setYearMonth)) continue;
            monthlyMaxE1RM.merge(setYearMonth, setE1RM, Math::max);
        }

        return new ExerciseStats(exercise.getName(), volume, sets, reps, maxWeight, bestRepSet, e1rm, monthlyMaxE1RM);
    }
}
