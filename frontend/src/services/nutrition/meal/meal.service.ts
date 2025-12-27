import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { MealDetailsDTO, MealDTO } from './meal.dto';

const BASE_URL = '/meals';

export const mealService = {

	getMeals: async (
		page: number,
		size: number
	): Promise<PageResponseDTO<MealDetailsDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			const { data } = await api.get<PageResponseDTO<MealDetailsDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch meals:', error);
			throw new Error('Unable to retrieve meals. Please try again.');
		}
	},

	createMeal: async (
		payload: MealDTO
	): Promise<MealDetailsDTO> => {
		try {
			console.log(payload);
			const { data } = await api.post(`${BASE_URL}`, payload);
			return data;
		} catch (error) {
			console.error('Failed to create meal:', error);
			throw new Error('Unable to create meal. Please try again.');
		}
	},

	updateMeal: async (
		id: number,
		payload: MealDTO
	): Promise<MealDetailsDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update meal with id ${id}:`, error);
			throw new Error(`Unable to update meal with id ${id}:. Please try again.`);
		}
	},

	deleteMeal: async (
		id: number
	): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete meal with id ${id}:`, error);
			throw new Error(`Unable to delete meal with id ${id}:. Please try again.`);
		}
	},

}