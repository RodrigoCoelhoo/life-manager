package com.rodrigocoelhoo.lifemanager.finances.controller;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.finances.dto.DashboardOverviewDTO;
import com.rodrigocoelhoo.lifemanager.finances.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/month-overview/{year_month}")
    public ResponseEntity<DashboardOverviewDTO> getMonthOverview(
            @PathVariable String year_month
    ) {
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(year_month);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid year_month format. Expected 'YYYY-MM'.");
        }

        return ResponseEntity.ok(dashboardService.getMonthOverview(yearMonth));
    }

}
