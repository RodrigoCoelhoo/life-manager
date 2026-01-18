package com.rodrigocoelhoo.lifemanager.training.dto.exercisedto;

import com.rodrigocoelhoo.lifemanager.training.service.TrainingDashboardService;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.Map;

public record ExerciseStats(
        String name,
        Double volume,
        Integer sets,
        Integer reps,
        Double maxWeight,
        TrainingDashboardService.ExercisePR.RepSet bestRepSet,
        Double e1rm,
        Map<YearMonth, Double> monthlyMaxE1RM
) implements Serializable {}