package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.nutrition.dto.DayDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.NutritionalLabelEntrieDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.WeekOverviewDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.MealIngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.MealModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalValueModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NutritionDashboardService {

    private final MealService mealService;

    public NutritionDashboardService(
            MealService mealService1
    ) {
        this.mealService = mealService1;
    }
    private static final String CACHE_DASHBOARD = "nutritionDashboard";

    @Cacheable(
            value = CACHE_DASHBOARD,
            key = "T(com.rodrigocoelhoo.lifemanager.config.RedisCacheService).getCurrentUsername() + " +
                    "'::week:' + T(com.rodrigocoelhoo.lifemanager.nutrition.service.NutritionDashboardService).getWeekStart(#date)"
    )
    public WeekOverviewDTO getWeekOverview(LocalDate date) {
        LocalDateTime start = date.with(DayOfWeek.MONDAY).atTime(0,0,0);
        LocalDateTime end = start.plusDays(6).toLocalDate().atTime(23,59,59);

        List<MealModel> meals = mealService.getMealsByRange(start, end);
        LinkedHashMap<LocalDate, List<MealModel>> week = populateMap(meals, start.toLocalDate(), end.toLocalDate());

        List<DayDTO> days = new ArrayList<>();
        for (LocalDate mealDate : week.keySet()) {
            days.add(getDayDTO(mealDate, week.get(mealDate)));
        }

        double totalCalories = 0;
        double totalProteins = 0;
        double totalFiber = 0;
        double totalCarbo = 0;
        double totalFat = 0;

        for (DayDTO day : days) {
            totalCalories += getNutrientValue(day, NutritionalTag.CALORIES);
            totalProteins += getNutrientValue(day, NutritionalTag.PROTEIN);
            totalFiber += getNutrientValue(day, NutritionalTag.FIBER);
            totalCarbo += getNutrientValue(day, NutritionalTag.CARBOHYDRATES);
            totalFat += getNutrientValue(day, NutritionalTag.FAT);
        }

        int daysCount = (int) week.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .count();
        if (daysCount == 0) daysCount = 1;

        WeekOverviewDTO.MacroTotalsDTO macroTotals = new WeekOverviewDTO.MacroTotalsDTO(
                totalCalories, totalCalories / daysCount,
                totalProteins, totalProteins / daysCount,
                totalCarbo, totalCarbo / daysCount,
                totalFat, totalFat / daysCount,
                totalFiber, totalFiber / daysCount
        );

        return new WeekOverviewDTO(macroTotals, days);
    }

    private DayDTO getDayDTO(LocalDate date, List<MealModel> mealModels) {
        LinkedHashMap<NutritionalTag, Double> totalDayNutrients = new LinkedHashMap<>();
        for (NutritionalTag tag : NutritionalTag.values()) {
            totalDayNutrients.put(tag, 0.0);
        }

        for (MealModel meal : mealModels) {
            for (MealIngredientModel ingredient : meal.getIngredients())
            {
                Set<NutritionalValueModel> nutrients = ingredient.getBrand().getNutritionalValues();

                for (NutritionalValueModel nutrient : nutrients) {
                    totalDayNutrients.merge(
                            nutrient.getTag(),
                            nutrient.getPer100units() * ingredient.getAmount() / 100,
                            Double::sum
                    );
                }
            }
        }

        for (NutritionalTag tag : NutritionalTag.values()) {
            if(totalDayNutrients.get(tag) == 0.0)
                totalDayNutrients.remove(tag);
        }

        return DayDTO.fromEntity(date, totalDayNutrients);
    }

    private LinkedHashMap<LocalDate, List<MealModel>> populateMap(List<MealModel> meals, LocalDate start, LocalDate end) {
        LinkedHashMap<LocalDate, List<MealModel>> result = new LinkedHashMap<>();

        LocalDate current = start;
        while (!current.isAfter(end)) {
            result.put(current, new ArrayList<>());
            current = current.plusDays(1);
        }

        for (MealModel meal : meals) {
            LocalDate mealDate = meal.getDate().toLocalDate();
            result.get(mealDate).add(meal);
        }

        return result;
    }

    private double getNutrientValue(DayDTO day, NutritionalTag tag) {
        return day.tags().stream()
                .filter(entry -> entry.nutrient().equals(tag.toString()))
                .mapToDouble(NutritionalLabelEntrieDTO::amount)
                .findFirst()
                .orElse(0.0);
    }

    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(java.time.DayOfWeek.MONDAY);
    }
}
