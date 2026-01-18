package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.training.repository.TrainingPlanRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final UserService userService;
    private final ExerciseService exerciseService;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "trainingPlans";

    public TrainingPlanService(
            TrainingPlanRepository trainingPlanRepository,
            UserService userService,
            ExerciseService exerciseService,
            RedisCacheService redisCacheService
    ) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.userService = userService;
        this.exerciseService = exerciseService;
        this.redisCacheService = redisCacheService;
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<TrainingPlanResponseDTO> getAllTrainingPlansByUser(Pageable pageable) {
        UserModel user = userService.getLoggedInUser();
        Page<TrainingPlanModel> page = trainingPlanRepository.findAllByUser(user, pageable);
        return page.map(TrainingPlanResponseDTO::fromEntity);
    }

    public TrainingPlanModel getTrainingPlan(Long id) {
        UserModel user = userService.getLoggedInUser();
        return trainingPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFound("Training plan with ID '" + id + "' does not belong to the current user"));
    }

    @Transactional
    public TrainingPlanModel createTrainingPlan(TrainingPlanDTO data) {
        UserModel user = userService.getLoggedInUser();
        TrainingPlanModel trainingPlanModel = TrainingPlanModel.builder()
                .user(user)
                .name(data.name())
                .description(data.description())
                .exercises(List.of())
                .build();

        TrainingPlanModel saved = trainingPlanRepository.save(trainingPlanModel);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public TrainingPlanModel updateTrainingPlan(Long id, TrainingPlanUpdateDTO data) {
        TrainingPlanModel plan = getTrainingPlan(id);
        List<ExerciseModel> exercises = exerciseService.getExercisesForUser(data.exerciseIds());

        exercises.sort(Comparator.comparingInt(
                e -> data.exerciseIds().indexOf(e.getId())
        ));

        plan.setName(data.name());
        plan.setDescription(data.description());
        plan.setExercises(exercises);

        TrainingPlanModel saved = trainingPlanRepository.save(plan);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public void deleteTrainingPlan(Long id) {
        UserModel user = userService.getLoggedInUser();

        TrainingPlanModel plan = trainingPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFound("Training plan with ID '" + id + "' does not belong to the current user"));

        trainingPlanRepository.delete(plan);
        redisCacheService.evictUserCache(CACHE_LIST);
    }
}
