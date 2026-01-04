import type { NutritionalLabelEntrieDTO } from "../meal/meal.dto";

export interface WeekOverviewDTO {
	macros: MacroTotalsDTO;
	week: DayDTO[];
}

export interface DayDTO {
	date: string;
	tags: NutritionalLabelEntrieDTO[];
}

export interface MacroTotalsDTO {
	totalCalories : number,
	avgCalories : number,
	totalProteins : number,
	avgProteins : number,
	totalCarbo : number,
	avgCarbo : number,
	totalFat : number,
	avgFat : number,
	totalFiber : number,
	avgFiber : number
}