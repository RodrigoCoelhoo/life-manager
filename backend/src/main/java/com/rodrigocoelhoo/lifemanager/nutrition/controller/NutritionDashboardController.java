package com.rodrigocoelhoo.lifemanager.nutrition.controller;

import com.rodrigocoelhoo.lifemanager.nutrition.dto.WeekOverviewDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.service.NutritionDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard/nutrition")
public class NutritionDashboardController {

    private final NutritionDashboardService nutritionDashboardService;

    public NutritionDashboardController(NutritionDashboardService nutritionDashboardService) {
        this.nutritionDashboardService = nutritionDashboardService;
    }

    @GetMapping("/week-overview/{date}")
    public ResponseEntity<WeekOverviewDTO> getWeekOverview(
            @PathVariable LocalDate date
    ) {
        return ResponseEntity.ok(nutritionDashboardService.getWeekOverview(date));
    }

}
