package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.training.dto.MonthOverviewDTO;
import com.rodrigocoelhoo.lifemanager.training.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TrainingDashboardServiceTest {

    @Mock
    private TrainingSessionService trainingSessionService;

    @InjectMocks
    private TrainingDashboardService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getMonthOverview")
    class GetMonthOverviewTests {

        @Test
        @DisplayName("should calculate volume, PRs, and dates for SET_REP exercises")
        void shouldCalculateSetRepStats() {
            ExerciseModel squat = ExerciseModel.builder()
                    .id(1L)
                    .name("Squat")
                    .type(ExerciseType.SET_REP)
                    .build();

            SessionExerciseModel set1 = new SessionExerciseModel();
            set1.setExercise(squat);
            set1.setWeight(100.0);
            set1.setReps(5);

            SessionExerciseModel set2 = new SessionExerciseModel();
            set2.setExercise(squat);
            set2.setWeight(110.0);
            set2.setReps(3);

            TrainingSessionModel session = TrainingSessionModel.builder()
                    .date(LocalDateTime.of(2026, 1, 10, 10, 0))
                    .exercises(List.of(set1, set2))
                    .build();

            when(trainingSessionService.getSessionsByRange(
                    YearMonth.of(2026, 1).atDay(1).atStartOfDay(),
                    YearMonth.of(2026, 1).atEndOfMonth().atTime(23, 59, 59)
            )).thenReturn(List.of(session));

            MonthOverviewDTO result = service.getMonthOverview(YearMonth.of(2026, 1));

            assertThat(result.volume()).isEqualTo(
                    (100 * 5) + (110 * 3)
            );

            assertThat(result.dates()).hasSize(1);
            assertThat(result.exercisePRS()).hasSize(1);
            assertThat(result.exercisePRS().getFirst().exerciseName()).isEqualTo("Squat");

            MonthOverviewDTO.ExercisePRDTO pr =
                    result.exercisePRS().getFirst();

            assertThat(pr.exerciseName()).isEqualTo("Squat");
            assertThat(pr.maxWeight()).isEqualTo(110);
            assertThat(pr.bestVolumeSet().reps()).isEqualTo(5);
            assertThat(pr.bestVolumeSet().weight()).isEqualTo(100);
            assertThat(pr.bestE1RM()).isGreaterThan(0);
        }

        @Test
        @DisplayName("should calculate time and distance for TIME exercises")
        void shouldCalculateTimeAndDistance() {
            ExerciseModel run = ExerciseModel.builder()
                    .id(2L)
                    .name("Running")
                    .type(ExerciseType.TIME)
                    .build();

            SessionExerciseModel set = new SessionExerciseModel();
            set.setExercise(run);
            set.setDurationSecs(1800);
            set.setDistance(5000);

            TrainingSessionModel session = TrainingSessionModel.builder()
                    .date(LocalDateTime.of(2026, 1, 5, 8, 0))
                    .exercises(List.of(set))
                    .build();

            when(trainingSessionService.getSessionsByRange(
                    YearMonth.of(2026, 1).atDay(1).atStartOfDay(),
                    YearMonth.of(2026, 1).atEndOfMonth().atTime(23, 59, 59)
            )).thenReturn(List.of(session));

            MonthOverviewDTO result = service.getMonthOverview(YearMonth.of(2026, 1));

            assertThat(result.timeSecs()).isEqualTo(1800);
            assertThat(result.distance()).isEqualTo(5000);
            assertThat(result.volume()).isEqualTo(0);
            assertThat(result.exercisePRS()).isEmpty();
        }

        @Test
        @DisplayName("should return empty overview when no sessions exist")
        void shouldReturnEmptyOverview() {
            when(trainingSessionService.getSessionsByRange(
                    YearMonth.of(2026, 1).atDay(1).atStartOfDay(),
                    YearMonth.of(2026, 1).atEndOfMonth().atTime(23, 59, 59)
            )).thenReturn(List.of());

            MonthOverviewDTO result = service.getMonthOverview(YearMonth.of(2026, 1));

            assertThat(result.dates()).isEmpty();
            assertThat(result.volume()).isZero();
            assertThat(result.timeSecs()).isZero();
            assertThat(result.distance()).isZero();
            assertThat(result.exercisePRS()).isEmpty();
        }
    }
}
