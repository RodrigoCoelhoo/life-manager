import type { RepSet } from "../dashboard/dashboard.dto";
import type { SessionExerciseSetRepDTO, SessionExerciseTimeDTO } from "../training-session/training-session.dto";

export const ExerciseType = {
  SET_REP: "SET_REP",
  TIME: "TIME",
} as const;
export type ExerciseType = (typeof ExerciseType)[keyof typeof ExerciseType];

export interface ExerciseDTO {
	name: string;
	description: string;
	type: ExerciseType;
	demoUrl: string;
}

export interface ExerciseResponseDTO {
	id: number;
	name: string;
	description: string;
	type: ExerciseType;
	demoUrl: string;
	createdAt: string;
	updatedAt: string;
}

export interface ExerciseSimpleDTO {
	id: number;
	name: string;
	type: ExerciseType;
	demoUrl: string;
}

export interface ExerciseDetailsDTO {
	id: number;
	name: string;
	type: ExerciseType;
	demoUrl: string;
	sets: (SessionExerciseSetRepDTO | SessionExerciseTimeDTO)[]
}

export interface ExerciseUpdateDTO {
	name: string;
	description: string;
	demoUrl: string;
}

export interface ExerciseStats {
	name: string;
	volume: number;
	sets: number;
	reps: number;
	maxWeight: number;
	bestRepSet: RepSet;
	e1rm: number;
	monthlyMaxE1RM: Record<string, number>;
}