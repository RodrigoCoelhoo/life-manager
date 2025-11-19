import type { ExerciseDetailsDTO } from "../exercise/exercise.dto";
import type { TrainingPlanDetailsDTO, TrainingPlanResponseDTO } from "../training-plan/training-plan.dto";

export interface TrainingSessionDTO {
	trainingPlanId: number;
	date: string;
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
	trainingPlan: TrainingPlanResponseDTO;
	date: string;
	createdAt: string;
	updatedAt: string;
}

export interface SessionDTO {
	id: number;
	trainingPlan: TrainingPlanResponseDTO;
	date: string;
}

export interface SessionExerciseDTO {
	setNumber: (number | null);
	reps: (number | null);
	weight: (number | null);

	durationSecs: (number | null);
	distance: (number | null);
}

export interface SessionExerciseBaseDTO {
	id: number;
	sessionId: number;
	exerciseId: number;
}

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
	session: SessionDTO;
	exercises: ExerciseDetailsDTO[];
}