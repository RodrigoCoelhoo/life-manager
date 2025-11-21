import type { SessionExerciseBaseDTO } from "../training-session/training-session.dto";

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
}

export interface ExerciseDetailsDTO {
	id: number;
	name: string;
	description: string;
	type: ExerciseType;
	sets: SessionExerciseBaseDTO[]
}

export interface ExerciseUpdateDTO {
	name: string;
	description: string;
	demoUrl: string;
}