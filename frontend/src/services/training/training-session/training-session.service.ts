import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { SessionDetailsDTO, SessionExerciseBaseDTO, TrainingSessionDetailsDTO, TrainingSessionDTO, TrainingSessionResponseDTO } from './training-session.dto';

const BASE_URL = '/training-sessions';

export const trainingSessionService = {

	getTrainingSessions: async (
		page: number,
		size: number
	): Promise<PageResponseDTO<TrainingSessionResponseDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			const { data } = await api.get<PageResponseDTO<TrainingSessionResponseDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch training sessions.', error);
			throw new Error('Unable to retrieve training sessions. Please try again.');
		}
	},

	getTrainingSession: async (id: number): Promise<TrainingSessionDetailsDTO> => {
		try {
			const { data } = await api.get<TrainingSessionDetailsDTO>(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to fetch training session with id: ${id}`, error);
			throw new Error(`Unable to retrieve training session with id: ${id}. Please try again.`);
		}
	},

	createTrainingSession: async (payload: TrainingSessionDTO): Promise<TrainingSessionResponseDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create training session', error);
			throw new Error('Unable to create training session. Please try again.');
		}
	},

	updateTrainingSession: async (
		id: number,
		payload: TrainingSessionDTO
	): Promise<TrainingSessionResponseDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update training session with id: ${id}`, error);
			throw new Error(`Unable to update training session with id: ${id}. Please try again.`);
		}
	},

	deleteTrainingSession: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete training session details with id: ${id}`, error);
			throw new Error(`Unable to delete training session with id: ${id}. Please try again.`);
		}
	},

	getTrainingSessionDetails: async (id: number): Promise<SessionDetailsDTO> => {
		try {
			const { data } = await api.get<SessionDetailsDTO>(`${BASE_URL}/${id}/details`);
			return data;
		} catch (error) {
			console.error(`Failed to fetch training session details with id: ${id}`, error);
			throw new Error(`Unable to retrieve training session details with id: ${id}. Please try again.`);
		}
	},

	createTrainingSessionExerciseDetails: async (
		sessionId: number,
		exerciseId: number,
		payload: TrainingSessionDTO
	): Promise<SessionExerciseBaseDTO> => {
		try {
			const { data } = await api.post<SessionExerciseBaseDTO>(`${BASE_URL}/${sessionId}/exercises/${exerciseId}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to create training session with id '${sessionId}' for exercise with id '${exerciseId}'`, error);
			throw new Error(`Unable to create training session with id '${sessionId}' for exercise with id '${exerciseId}'`);
		}
	},

	updateTrainingSessionExerciseDetails: async (
		sessionId: number,
		exerciseId: number,
		sessionSetId: number,
		payload: TrainingSessionDTO
	): Promise<SessionExerciseBaseDTO> => {
		try {
			const { data } = await api.put<SessionExerciseBaseDTO>(`${BASE_URL}/${sessionId}/exercises/${exerciseId}/sets/${sessionSetId}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update training session with id '${sessionId}' for exercise with id '${exerciseId}'`, error);
			throw new Error(`Unable to update training session with id '${sessionId}' for exercise with id '${exerciseId}'`);
		}
	},

	deleteTrainingSessionExerciseDetails: async (
		sessionId: number,
		exerciseId: number,
		sessionSetId: number
	): Promise<void> => {
		try {
			const { data } = await api.delete<void>(`${BASE_URL}/${sessionId}/exercises/${exerciseId}/sets/${sessionSetId}`);
			return data;
		} catch (error) {
			console.error(`Failed to update training session with id '${sessionId}' for exercise with id '${exerciseId}'`, error);
			throw new Error(`Unable to update training session with id '${sessionId}' for exercise with id '${exerciseId}'`);
		}
	},

}