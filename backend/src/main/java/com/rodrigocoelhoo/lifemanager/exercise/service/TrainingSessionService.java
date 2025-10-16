package com.rodrigocoelhoo.lifemanager.exercise.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.exercise.dto.trainingsessiondto.TrainingSessionDTO;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.exercise.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.exercise.repository.TrainingSessionRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final UserService userService;
    private final TrainingPlanService trainingPlanService;

    public TrainingSessionService(
            TrainingSessionRepository trainingSessionRepository,
            UserService userService,
            TrainingPlanService trainingPlanService
    ) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userService = userService;
        this.trainingPlanService = trainingPlanService;
    }

    public List<TrainingSessionModel> getAllSessions() {
        UserModel user = userService.getLoggedInUser();
        return trainingSessionRepository.findAllByUser(user);
    }

    public TrainingSessionModel getSession(Long id) {
        UserModel user = userService.getLoggedInUser();
        return trainingSessionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFound("Session with ID '" + id + "' does not belong to the current user"));
    }

    public TrainingSessionModel createSession(TrainingSessionDTO data) {
        UserModel user = userService.getLoggedInUser();
        TrainingPlanModel plan = trainingPlanService.getTrainingPlan(data.trainingPlanId());

        TrainingSessionModel session = TrainingSessionModel.builder()
                .user(user)
                .trainingPlan(plan)
                .date(data.date())
                .build();

        return trainingSessionRepository.save(session);
    }

    public TrainingSessionModel updateSession(Long id, TrainingSessionDTO data) {
        TrainingSessionModel session = getSession(id);
        TrainingPlanModel plan = trainingPlanService.getTrainingPlan(data.trainingPlanId());

        session.setTrainingPlan(plan);
        session.setDate(data.date());

        return trainingSessionRepository.save(session);
    }

    public void deleteSession(Long id) {
        TrainingSessionModel session = getSession(id);
        trainingSessionRepository.delete(session);
    }
}
