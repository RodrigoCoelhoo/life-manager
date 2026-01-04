export interface MonthOverviewDTO {
	totalSessions: number;
	dates: string[];
	volume: number;
	timeSecs: number;
	distance: number;
	exercisePRS: ExercisePRDTO[];
}

export interface ExercisePRDTO {
	exerciseName: string;
	bestE1RM: number;
	maxWeight: number;
	bestVolumeSet: RepSet;
}

export interface RepSet {
	reps: number;
	weight: number;
}