package com.rodrigocoelhoo.lifemanager.training.dto;

import com.rodrigocoelhoo.lifemanager.training.service.TrainingDashboardService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public record MonthOverviewDTO(
        List<LocalDate> dates,
        Double volume,
        Integer timeSecs,
        Double distance,
        List<ExercisePRDTO> exercisePRS
    ) {
    public static MonthOverviewDTO fromEntities(
            List<LocalDate> dates,
            double volume,
            int timeSecs,
            double distance,
            HashMap<Long, TrainingDashboardService.ExercisePR> exercisePr
    ) {
        return new MonthOverviewDTO(
            dates,
            volume,
            timeSecs,
            distance,
            exercisePr.values().stream().map(exercisePR -> new ExercisePRDTO(
                            exercisePR.name(),
                            exercisePR.bestE1RM(),
                            exercisePR.maxWeight(),
                            exercisePR.bestRepSet()
                    )
            ).toList()
        );
    }

    public record ExercisePRDTO(
            String exerciseName,
            double bestE1RM,
            double maxWeight,
            TrainingDashboardService.ExercisePR.RepSet bestVolumeSet
    ) { }
}
