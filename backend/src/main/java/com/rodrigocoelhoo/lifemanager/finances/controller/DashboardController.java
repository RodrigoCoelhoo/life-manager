package com.rodrigocoelhoo.lifemanager.finances.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.DashboardOverviewDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.service.DashboardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/month-overview/{yearMonth}/{currency}")
    public ResponseEntity<DashboardOverviewDTO> getMonthOverview(
            @PathVariable YearMonth yearMonth,
            @PathVariable @Valid Currency currency
            ) {
        return ResponseEntity.ok(dashboardService.getMonthOverview(yearMonth, currency));
    }
}
