import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { TrainingPlanDetailsDTO, TrainingPlanDTO, TrainingPlanResponseDTO, TrainingPlanUpdateDTO } from './training-plan.dto';

const BASE_URL = '/training-plans';

export const trainingPlanService = {

	getTrainingPlans: async (page: number, size: number): Promise<PageResponseDTO<TrainingPlanResponseDTO>> => {
		try {
			const { data } = await api.get<PageResponseDTO<TrainingPlanResponseDTO>>(`${BASE_URL}?page=${page}&size=${size}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch training plans.', error);
			throw new Error('Unable to retrieve training plans. Please try again.');
		}
	},

	getTrainingPlan: async (id: number): Promise<TrainingPlanDetailsDTO> => {
		try {
			const { data } = await api.get<TrainingPlanDetailsDTO>(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to fetch training plan with id: ${id}`, error);
			throw new Error(`Unable to retrieve training plan with id: ${id}. Please try again.`);
		}
	},

	createTrainingPlan: async (payload: TrainingPlanDTO): Promise<TrainingPlanResponseDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create training plan', error);
			throw new Error('Unable to create training plan. Please try again.');
		}
	},

	updateTrainingPlan: async (
		id: number,
		payload: TrainingPlanUpdateDTO
	): Promise<TrainingPlanDetailsDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update training plan with id: ${id}`, error);
			throw new Error(`Unable to update training plan with id: ${id}. Please try again.`);
		}
	},

	deleteTrainingPlan: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete training plan with id: ${id}`, error);
			throw new Error(`Unable to delete training plan with id: ${id}. Please try again.`);
		}
	},

}