package com.rodrigocoelhoo.lifemanager.training.controller;

import com.rodrigocoelhoo.lifemanager.training.dto.MonthOverviewDTO;
import com.rodrigocoelhoo.lifemanager.training.service.TrainingDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard/training")
public class TrainingDashboardController {

    private final TrainingDashboardService trainingDashboardService;

    public TrainingDashboardController(TrainingDashboardService trainingDashboardService) {
        this.trainingDashboardService = trainingDashboardService;
    }

    @GetMapping("/month-overview/{date}")
    public ResponseEntity<MonthOverviewDTO> getWeekOverview(
            @PathVariable YearMonth date
    ) {
        return ResponseEntity.ok(trainingDashboardService.getMonthOverview(date));
    }
}
