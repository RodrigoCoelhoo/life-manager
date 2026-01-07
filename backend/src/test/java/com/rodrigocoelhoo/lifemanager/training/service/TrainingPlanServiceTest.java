package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingplandto.TrainingPlanUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingPlanModel;
import com.rodrigocoelhoo.lifemanager.training.repository.TrainingPlanRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("TrainingPlanService Tests")
class TrainingPlanServiceTest {

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private TrainingPlanService trainingPlanService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.getLoggedInUser()).thenReturn(user);
    }

    @Nested
    @DisplayName("getAllTrainingPlansByUser")
    class GetAllTrainingPlansTests {

        @Test
        @DisplayName("should return all training plans for logged-in user")
        void shouldReturnAllTrainingPlans() {
            TrainingPlanModel plan = TrainingPlanModel.builder().user(user).build();
            Page<TrainingPlanModel> page = new PageImpl<>(List.of(plan));

            when(trainingPlanRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<TrainingPlanModel> result = trainingPlanService.getAllTrainingPlansByUser(Pageable.unpaged());

            assertThat(result.getContent()).hasSize(1).containsExactly(plan);
            verify(trainingPlanRepository).findAllByUser(user, Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getTrainingPlan")
    class GetTrainingPlanTests {

        @Test
        @DisplayName("should return training plan if it belongs to user")
        void shouldReturnTrainingPlan() {
            TrainingPlanModel plan = TrainingPlanModel.builder().id(1L).user(user).build();
            when(trainingPlanRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(plan));

            TrainingPlanModel result = trainingPlanService.getTrainingPlan(1L);

            assertThat(result).isEqualTo(plan);
            verify(trainingPlanRepository).findByIdAndUser(1L, user);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if plan does not belong to user")
        void shouldThrowIfNotFound() {
            when(trainingPlanRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> trainingPlanService.getTrainingPlan(1L)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Training plan with ID '1' does not belong to the current user");
        }
    }

    @Nested
    @DisplayName("createTrainingPlan")
    class CreateTrainingPlanTests {

        @Test
        @DisplayName("should create training plan successfully")
        void shouldCreateTrainingPlan() {
            TrainingPlanDTO dto = new TrainingPlanDTO("Plan 1", "Description");
            TrainingPlanModel plan = TrainingPlanModel.builder().user(user).name("Plan 1").description("Description").exercises(new ArrayList<>()).build();

            when(trainingPlanRepository.save(any())).thenReturn(plan);

            TrainingPlanModel result = trainingPlanService.createTrainingPlan(dto);

            assertThat(result.getName()).isEqualTo("Plan 1");
            assertThat(result.getUser()).isEqualTo(user);
            verify(trainingPlanRepository).save(any());
        }
    }

    @Nested
    @DisplayName("updateTrainingPlan")
    class UpdateTrainingPlanTests {

        @Test
        @DisplayName("should update training plan successfully")
        void shouldUpdateTrainingPlan() {
            TrainingPlanModel existingPlan = TrainingPlanModel.builder().id(1L).user(user).exercises(new ArrayList<>()).build();
            TrainingPlanUpdateDTO dto = new TrainingPlanUpdateDTO("Updated Plan", "Updated Desc", List.of(1L,2L));

            ExerciseModel ex1 = new ExerciseModel();
            ex1.setId(1L);

            ExerciseModel ex2 = new ExerciseModel();
            ex2.setId(2L);

            when(trainingPlanRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(existingPlan));
            when(exerciseService.getExercisesForUser(List.of(1L,2L)))
                    .thenReturn(new ArrayList<>(List.of(ex1, ex2)));
            when(trainingPlanRepository.save(existingPlan)).thenReturn(existingPlan);

            TrainingPlanModel result = trainingPlanService.updateTrainingPlan(1L, dto);

            assertThat(result.getName()).isEqualTo("Updated Plan");
            assertThat(result.getExercises()).containsExactly(ex1, ex2);
            verify(trainingPlanRepository).save(existingPlan);
        }
    }

    @Nested
    @DisplayName("deleteTrainingPlan")
    class DeleteTrainingPlanTests {

        @Test
        @DisplayName("should delete training plan successfully")
        void shouldDeleteTrainingPlan() {
            TrainingPlanModel plan = TrainingPlanModel.builder().id(1L).user(user).build();
            when(trainingPlanRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(plan));

            trainingPlanService.deleteTrainingPlan(1L);

            verify(trainingPlanRepository).delete(plan);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if plan does not exist")
        void shouldThrowIfNotFound() {
            when(trainingPlanRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.empty());

            assertThrows(ResourceNotFound.class, () -> trainingPlanService.deleteTrainingPlan(1L));
        }
    }
}
