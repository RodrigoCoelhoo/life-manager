import type { ExerciseDetailsDTO, ExerciseSimpleDTO } from "../exercise/exercise.dto";
import type { TrainingPlanDetailsDTO } from "../training-plan/training-plan.dto";

export interface TrainingSessionDTO {
	date: string;
	exercises: SessionExerciseDTO[];
}

export interface TrainingSessionDetailsDTO {
	id: number;
	trainingPlan: TrainingPlanDetailsDTO;
	date: string;
	createdAt: string;
	updatedAt: string;
}

export interface TrainingSessionResponseDTO {
	id: number;
	date: string;
	exercises: ExerciseSimpleDTO[];
	createdAt: string;
	updatedAt: string;
}

export interface SessionSimpleDTO {
	id: number;
	date: string;
}

export interface SessionExerciseDTO {
	exerciseId: number;
	sets: SessionExerciseSetDTO[];
}

export interface SessionExerciseSetDTO {
	setNumber: (number | null);
	reps: (number | null);
	weight: (number | null);

	durationSecs: (number | null);
	distance: (number | null);
}

export interface SessionExerciseBaseDTO {}

export interface SessionExerciseSetRepDTO extends SessionExerciseBaseDTO {
	setNumber: number;
	reps: number;
	weight: number;
}

export interface SessionExerciseTimeDTO extends SessionExerciseBaseDTO {
	durationSecs: number;
	distance: number;
}

export interface SessionDetailsDTO {
	session: SessionSimpleDTO;
	exercises: ExerciseDetailsDTO[];
}