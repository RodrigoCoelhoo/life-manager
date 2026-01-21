package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseResponseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseStats;
import com.rodrigocoelhoo.lifemanager.training.dto.exercisedto.ExerciseUpdateDTO;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.ExerciseType;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import com.rodrigocoelhoo.lifemanager.training.model.TrainingSessionModel;
import com.rodrigocoelhoo.lifemanager.training.repository.ExerciseRepository;
import com.rodrigocoelhoo.lifemanager.training.repository.SessionExerciseRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("ExerciseService Tests")
class ExerciseServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private SessionExerciseRepository sessionExerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Mock
    private RedisCacheService redisCacheService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getLoggedInUser()).thenReturn(user);
        doNothing().when(redisCacheService).evictUserCache(anyString());
        doNothing().when(redisCacheService).evictUserCacheSpecific(anyString(), anyString());
    }

    @Nested
    @DisplayName("getAllExercisesByUser")
    class GetAllExercisesTests {

        @Test
        @DisplayName("should return all exercises when name is null")
        void shouldReturnAllExercisesWhenNameNull() {
            ExerciseModel ex1 = new ExerciseModel();
            ExerciseModel ex2 = new ExerciseModel();
            Page<ExerciseModel> page = new PageImpl<>(List.of(ex1, ex2));
            when(exerciseRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<ExerciseResponseDTO> result = exerciseService.getAllExercisesByUser(Pageable.unpaged(), null);

            assertThat(result.getContent()).hasSize(2).containsExactlyInAnyOrder(
                    ExerciseResponseDTO.fromEntity(ex1),
                    ExerciseResponseDTO.fromEntity(ex2)
            );
            verify(exerciseRepository).findAllByUser(user, Pageable.unpaged());
        }

        @Test
        @DisplayName("should return filtered exercises when name is provided")
        void shouldReturnFilteredExercises() {
            ExerciseModel ex = new ExerciseModel();
            Page<ExerciseModel> page = new PageImpl<>(List.of(ex));
            when(exerciseRepository.findByUserAndNameContainingIgnoreCase(user, "Squat", Pageable.unpaged()))
                    .thenReturn(page);

            Page<ExerciseResponseDTO> result = exerciseService.getAllExercisesByUser(Pageable.unpaged(), "Squat");

            assertThat(result.getContent()).hasSize(1).containsExactly(ExerciseResponseDTO.fromEntity(ex));
            verify(exerciseRepository).findByUserAndNameContainingIgnoreCase(user, "Squat", Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getExercise")
    class GetExerciseTests {

        @Test
        @DisplayName("should return exercise if it belongs to the user")
        void shouldReturnExerciseIfBelongsToUser() {
            ExerciseModel exercise = new ExerciseModel();
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(exercise));

            ExerciseModel result = exerciseService.getExercise(1L);

            assertThat(result).isEqualTo(exercise);
            verify(exerciseRepository).findByIdAndUser(1L, user);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if exercise does not belong to user")
        void shouldThrowIfExerciseNotBelongToUser() {
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.empty());

            ResourceNotFound ex = assertThrows(ResourceNotFound.class,
                    () -> exerciseService.getExercise(1L));

            assertThat(ex.getMessage()).contains("Exercise with ID '1' does not belong to the current user");
        }
    }

    @Nested
    @DisplayName("create")
    class CreateExerciseTests {

        @Test
        @DisplayName("should create exercise successfully")
        void shouldCreateExerciseSuccessfully() {
            ExerciseDTO dto = new ExerciseDTO("Squat", "Desc", "TIME", null);
            ExerciseModel saved = new ExerciseModel();
            saved.setName(dto.name());

            when(exerciseRepository.save(any())).thenReturn(saved);

            ExerciseModel result = exerciseService.create(dto);

            assertThat(result.getName()).isEqualTo("Squat");
            verify(exerciseRepository).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateExerciseTests {

        @Test
        @DisplayName("should update exercise successfully")
        void shouldUpdateExercise() {
            ExerciseModel existing = new ExerciseModel();
            existing.setName("Old Name");
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(existing));
            when(exerciseRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            ExerciseUpdateDTO dto = new ExerciseUpdateDTO("New Name", "New Desc", "demoUrl");

            ExerciseModel result = exerciseService.update(1L, dto);

            assertThat(result.getName()).isEqualTo("New Name");
            assertThat(result.getDescription()).isEqualTo("New Desc");
            assertThat(result.getDemoUrl()).isEqualTo("demoUrl");
        }

        @Test
        @DisplayName("should throw ResourceNotFound if exercise not found")
        void shouldThrowIfExerciseNotFound() {
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.empty());

            ExerciseUpdateDTO dto = new ExerciseUpdateDTO("Name", "Desc", null);

            Exception exception = assertThrows(ResourceNotFound.class, () -> exerciseService.update(1L, dto));
            assertThat(exception.getMessage()).isEqualTo("Exercise with ID '1' does not belong to the current user");
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteExerciseTests {

        @Test
        @DisplayName("should delete exercise successfully")
        void shouldDeleteExerciseSuccessfully() {
            ExerciseModel exercise = new ExerciseModel();
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(exercise));

            exerciseService.delete(1L);

            verify(exerciseRepository).delete(exercise);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if exercise does not exist")
        void shouldThrowIfExerciseDoesNotExist() {
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.empty());

            Exception exception = assertThrows(ResourceNotFound.class, () -> exerciseService.delete(1L));
            assertThat(exception.getMessage()).isEqualTo("Exercise with ID '1' does not belong to the current user");
        }
    }

    @Nested
    @DisplayName("getExerciseStats")
    class GetExerciseStatsTests {

        @Test
        @DisplayName("should return null if exercise type is TIME")
        void shouldReturnNullForTimeType() {
            ExerciseModel exercise = new ExerciseModel();
            exercise.setType(ExerciseType.TIME);
            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(exercise));

            ExerciseStats stats = exerciseService.getExerciseStats(1L);

            assertThat(stats).isNull();
        }

        @Test
        @DisplayName("should calculate stats for non-TIME exercise")
        void shouldCalculateStats() {
            // Create the exercise
            ExerciseModel exercise = new ExerciseModel();
            exercise.setId(1L);
            exercise.setName("Bench Press");
            exercise.setType(ExerciseType.SET_REP);

            TrainingSessionModel session = new TrainingSessionModel();
            session.setId(1L);
            session.setUser(user);
            session.setDate(LocalDateTime.of(2026, 1, 1, 16, 0, 0));

            when(exerciseRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(exercise));

            // Mock session exercises
            SessionExerciseModel session1 = SessionExerciseModel.builder()
                    .session(session)
                    .reps(5)
                    .weight(100.0)
                    .exercise(exercise)
                    .build();

            SessionExerciseModel session2 = SessionExerciseModel.builder()
                    .session(session)
                    .reps(3)
                    .weight(120.0)
                    .exercise(exercise)
                    .build();

            List<SessionExerciseModel> sessions = List.of(session1, session2);
            when(sessionExerciseRepository.findAllByExercise(exercise)).thenReturn(sessions);

            ExerciseStats stats = exerciseService.getExerciseStats(1L);

            double expectedVolume = (5 * 100.0) + (3 * 120.0); // 500 + 360 = 860
            int expectedReps = 5 + 3; // 8
            int expectedSets = sessions.size(); // 2
            double expectedMaxWeight = 120.0;
            double expectedE1RM1 = 100 * (1 + 5 / 30.0); // 116.666...
            double expectedMaxE1RM = 120 * (1 + 3 / 30.0); // 132.0

            assertThat(stats).isNotNull();
            assertThat(stats.volume()).isEqualTo(expectedVolume);
            assertThat(stats.reps()).isEqualTo(expectedReps);
            assertThat(stats.sets()).isEqualTo(expectedSets);
            assertThat(stats.maxWeight()).isEqualTo(expectedMaxWeight);
            assertThat(stats.e1rm()).isEqualTo(expectedMaxE1RM);

            int bestReps = 5;
            double bestWeight = 100.0;
            assertThat(stats.bestRepSet().reps()).isEqualTo(bestReps);
            assertThat(stats.bestRepSet().weight()).isEqualTo(bestWeight);

            verify(sessionExerciseRepository).findAllByExercise(exercise);
        }

    }
}
