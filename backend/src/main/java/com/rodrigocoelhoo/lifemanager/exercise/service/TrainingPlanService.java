package com.rodrigocoelhoo.lifemanager.exercise.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanDTO;
import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingplandto.TrainingPlanUpdateDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.exercise.repository.TrainingPlanRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final UserService userService;
    private final ExerciseService exerciseService;

    public TrainingPlanService(
            TrainingPlanRepository trainingPlanRepository,
            UserService userService,
            ExerciseService exerciseService
    ) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.userService = userService;
        this.exerciseService = exerciseService;
    }

    public List<TrainingPlanModel> getAllTrainingPlansByUser() {
        UserModel user = userService.getLoggedInUser();
        return trainingPlanRepository.findAllByUser(user);
    }

    public TrainingPlanModel getTrainingPlan(Long id) {
        UserModel user = userService.getLoggedInUser();
        return trainingPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFound("Training plan with ID '" + id + "' does not belong to the current user"));
    }

    public TrainingPlanModel createTrainingPlan(TrainingPlanDTO data) {
        UserModel user = userService.getLoggedInUser();
        TrainingPlanModel trainingPlanModel = TrainingPlanModel.builder()
                .user(user)
                .name(data.name())
                .description(data.description())
                .exercises(List.of())
                .build();

        return trainingPlanRepository.save(trainingPlanModel);
    }

    public TrainingPlanModel updateTrainingPlan(Long id, TrainingPlanUpdateDTO data) {
        TrainingPlanModel plan = getTrainingPlan(id);
        List<ExerciseModel> exercises = exerciseService.getExercisesForUser(data.exerciseIds());

        plan.setName(data.name());
        plan.setDescription(data.description());
        plan.setExercises(exercises);

        return trainingPlanRepository.save(plan);
    }

    public void deleteTrainingPlan(Long id) {
        UserModel user = userService.getLoggedInUser();

        TrainingPlanModel plan = trainingPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFound("Training plan with ID '" + id + "' does not belong to the current user"));

        trainingPlanRepository.delete(plan);
    }
}
