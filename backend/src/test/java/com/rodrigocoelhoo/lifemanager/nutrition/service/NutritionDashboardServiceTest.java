package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.DayDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.WeekOverviewDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.*;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.MealRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@DisplayName("NutritionDashboardService Tests")
class NutritionDashboardServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NutritionDashboardService dashboardService;

    @Mock
    private MealService mealService;

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
    @DisplayName("getWeekOverview")
    class GetWeekOverviewTests {

        @Test
        @DisplayName("should calculate correct macro totals for a week")
        void shouldCalculateWeekOverviewCorrectly() {
            LocalDate monday = LocalDate.of(2026, 1, 5); // Monday
            LocalDateTime mealTime = monday.atTime(12, 0);


            IngredientModel ingredient = IngredientModel.builder()
                    .id(1L)
                    .brands(new HashSet<>())
                    .build();

            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .ingredient(ingredient)
                    .nutritionalValues(new HashSet<>())
                    .build();

            brand.getNutritionalValues().addAll(List.of(
                    NutritionalValueModel.builder()
                            .tag(NutritionalTag.CALORIES)
                            .per100units(200.0)
                            .build(),
                    NutritionalValueModel.builder()
                            .tag(NutritionalTag.PROTEIN)
                            .per100units(10.0)
                            .build()
            ));
            ingredient.getBrands().add(brand);

            MealIngredientModel mealIngredient = MealIngredientModel.builder()
                    .ingredient(ingredient)
                    .brand(brand)
                    .amount(100.0)
                    .unit(Unit.G)
                    .build();

            MealModel meal = MealModel.builder()
                    .user(user)
                    .date(mealTime)
                    .ingredients(new HashSet<>())
                    .build();

            meal.getIngredients().add(mealIngredient);

            when(mealService.getMealsByRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(meal));


            WeekOverviewDTO overview = dashboardService.getWeekOverview(monday);

            assertThat(overview.week()).hasSize(7);
            DayDTO day = overview.week().getFirst();
            assertThat(day.date()).isEqualTo(monday);
            assertThat(day.tags()).anySatisfy(entry -> {
                assertThat(entry.nutrient()).isEqualTo(NutritionalTag.CALORIES.toString());
                assertThat(entry.amount()).isEqualTo(200.0);
            });
            assertThat(day.tags()).anySatisfy(entry -> {
                assertThat(entry.nutrient()).isEqualTo(NutritionalTag.PROTEIN.toString());
                assertThat(entry.amount()).isEqualTo(10.0);
            });

            assertThat(overview.macros().totalCalories()).isEqualTo(200.0);
            assertThat(overview.macros().avgCalories()).isEqualTo(200.0);
        }

        @Test
        @DisplayName("should handle empty week with no meals")
        void shouldReturnEmptyOverviewForWeekWithNoMeals() {
            LocalDate monday = LocalDate.of(2026, 1, 6);

            when(mealRepository.findAllByUserAndDateBetweenOrderByDateDescIdDesc(
                    user,
                    monday.with(java.time.DayOfWeek.MONDAY).atTime(0,0),
                    monday.with(java.time.DayOfWeek.MONDAY).atTime(23,59,59).plusDays(6)
            )).thenReturn(List.of());

            WeekOverviewDTO overview = dashboardService.getWeekOverview(monday);

            assertThat(overview.week()).hasSize(7);
            for (DayDTO day : overview.week()) {
                assertThat(day.tags()).isEmpty();
            }

            assertThat(overview.macros().totalCalories()).isEqualTo(0.0);
            assertThat(overview.macros().avgCalories()).isEqualTo(0.0);
        }
    }
}
