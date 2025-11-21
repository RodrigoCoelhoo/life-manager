import type { ExerciseResponseDTO, ExerciseSimpleDTO } from "../exercise/exercise.dto";

export interface TrainingPlanDTO {
	name: string;
	description: string;
}

export interface TrainingPlanResponseDTO {
	id: number;
	name: string;
	description: string;
	exercises: ExerciseSimpleDTO[];
	createdAt: string;
	updatedAt: string;
}

export interface TrainingPlanDetailsDTO {
	id: number;
	name: string;
	description: string;
	exercises: ExerciseResponseDTO[];
	createdAt: string;
	updatedAt: string;
}

export interface TrainingPlanUpdateDTO {
	name: string;
	description: string;
	exerciseIds: number[];
}