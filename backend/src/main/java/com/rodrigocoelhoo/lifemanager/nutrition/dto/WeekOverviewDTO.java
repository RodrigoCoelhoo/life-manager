package com.rodrigocoelhoo.lifemanager.nutrition.dto;

import java.util.List;

public record WeekOverviewDTO(
        MacroTotalsDTO macros,
        List<DayDTO> week
) {
    public record MacroTotalsDTO(
            double totalCalories,
            double avgCalories,
            double totalProteins,
            double avgProteins,
            double totalCarbo,
            double avgCarbo,
            double totalFat,
            double avgFat,
            double totalFiber,
            double avgFiber
    ) { }
}
