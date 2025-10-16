package com.rodrigocoelhoo.lifemanager.exercise.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.exercise.dto.exercisedto.ExerciseDTO;
import com.rodrigocoelhoo.lifemanager.exercise.dto.exercisedto.ExerciseUpdateDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.exercise.model.ExerciseType;
import com.rodrigocoelhoo.lifemanager.exercise.repository.ExerciseRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    private final UserService userService;

    public ExerciseService(ExerciseRepository exerciseRepository, UserService userService) {
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
    }

    public List<ExerciseModel> getAllExercisesByUser() {
        UserModel user = userService.getLoggedInUser();
        return exerciseRepository.findAllByUser(user);
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

        return exerciseRepository.save(exercise);
    }

    public ExerciseModel update(Long exerciseId, ExerciseUpdateDTO data) {
        ExerciseModel exercise = getExercise(exerciseId);

        exercise.setName(data.name());
        exercise.setDescription(data.description());
        exercise.setDemoUrl(data.demoUrl());

        return exerciseRepository.save(exercise);
    }

    public void delete(Long exerciseId) {
        ExerciseModel exercise = getExercise(exerciseId);
        exerciseRepository.delete(exercise);
    }
}
