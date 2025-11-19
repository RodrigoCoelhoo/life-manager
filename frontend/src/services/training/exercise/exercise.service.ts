import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { ExerciseDTO, ExerciseResponseDTO, ExerciseUpdateDTO } from './exercise.dto';

const BASE_URL = '/exercises';

export const exerciseService = {

	getExercises: async (page: number, size: number): Promise<PageResponseDTO<ExerciseResponseDTO>> => {
		try {
			const { data } = await api.get<PageResponseDTO<ExerciseResponseDTO>>(`${BASE_URL}?page=${page}&size=${size}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch exercises.', error);
			throw new Error('Unable to retrieve exercises. Please try again.');
		}
	},

	getExercise: async (id: number): Promise<ExerciseResponseDTO> => {
		try {
			const { data } = await api.get<ExerciseResponseDTO>(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to fetch exercise with id: ${id}`, error);
			throw new Error(`Unable to retrieve exercise with id: ${id}. Please try again.`);
		}
	},

	createExercise: async (payload: ExerciseDTO): Promise<ExerciseResponseDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create exercise', error);
			throw new Error('Unable to create exercise. Please try again.');
		}
	},

	updateExercise: async (
		id: number,
		payload: ExerciseUpdateDTO
	): Promise<ExerciseResponseDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update exercise with id: ${id}`, error);
			throw new Error(`Unable to update exercise with id: ${id}. Please try again.`);
		}
	},

	deleteExercise: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete exercise with id: ${id}`, error);
			throw new Error(`Unable to delete exercise with id: ${id}. Please try again.`);
		}
	},

}