import { api } from '../../api';
import type { IngredientBrandDetailsResponseDTO, IngredientBrandDTO } from './ingredient-brand.dto';

const BASE_URL = '/ingredients';

export const ingredientBrandService = {

	createIngredientBrand: async (
		ingredientId: number,
		payload: IngredientBrandDTO
	): Promise<IngredientBrandDetailsResponseDTO> => {
		try {
			console.log(payload);
			const { data } = await api.post(`${BASE_URL}/${ingredientId}/brands`, payload);
			return data;
		} catch (error) {
			console.error('Failed to create ingredient brand:', error);
			throw new Error('Unable to create ingredient brand. Please try again.');
		}
	},

	updateIngredientBrand: async (
		ingredientId: number,
		brandId: number,
		payload: IngredientBrandDTO
	): Promise<IngredientBrandDetailsResponseDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${ingredientId}/brands/${brandId}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update ingredient brand with id ${brandId}:`, error);
			throw new Error(`Unable to update ingredient brand with id ${brandId}:. Please try again.`);
		}
	},

	deleteIngredientBrand: async (
		ingredientId: number,
		brandId: number
	): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${ingredientId}/brands/${brandId}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete ingredient brand with id ${brandId}:`, error);
			throw new Error(`Unable to delete ingredient brand with id ${brandId}:. Please try again.`);
		}
	},

}