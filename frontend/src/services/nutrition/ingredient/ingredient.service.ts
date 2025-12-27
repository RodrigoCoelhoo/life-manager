import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { IngredientDetailsDTO, IngredientDTO, IngredientResponseDTO } from './ingredient.dto';

const BASE_URL = '/ingredients';

export const ingredientService = {

	getIngredients: async (
		page: number,
		size: number,
		name?: string
	): Promise<PageResponseDTO<IngredientDetailsDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			if (name && name.trim() !== "") {
				params.append("name", name);
			}

			const { data } = await api.get<PageResponseDTO<IngredientDetailsDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch ingredients:', error);
			throw new Error('Unable to retrieve ingredients. Please try again.');
		}
	},

	getIngredientsByIds: async (
		ids: number[]
	): Promise<IngredientResponseDTO[]> => {
		try {
			const params = new URLSearchParams();
			ids.forEach(id => params.append("ids", id.toString()));

			const { data } = await api.get<IngredientResponseDTO[]>(`${BASE_URL}/by-ids?${params.toString()}`);
			return data;
		} catch (error) {
			console.error("Failed to fetch ingredients by ids:", error);
			throw new Error("Unable to retrieve ingredients.");
		}
	},

	createIngredient: async (payload: IngredientDTO): Promise<IngredientDetailsDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create ingredient:', error);
			throw new Error('Unable to create ingredient. Please try again.');
		}
	},

	updateIngredient: async (
		id: number,
		payload: IngredientDTO
	): Promise<IngredientDetailsDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update ingredient with id ${id}:`, error);
			throw new Error(`Unable to update ingredient with id ${id}:. Please try again.`);
		}
	},

	deleteIngredient: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete ingredient with id ${id}:`, error);
			throw new Error(`Unable to delete ingredient with id ${id}:. Please try again.`);
		}
	},

}