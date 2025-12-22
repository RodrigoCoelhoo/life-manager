import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { RecipeDetailsDTO, RecipeDTO } from './recipe.dto';

const BASE_URL = '/recipes';

export const recipeService = {

	getRecipes: async (
		page: number,
		size: number,
		name?: string
	): Promise<PageResponseDTO<RecipeDetailsDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			if (name && name.trim() !== "") {
				params.append("name", name);
			}

			const { data } = await api.get<PageResponseDTO<RecipeDetailsDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch recipes:', error);
			throw new Error('Unable to retrieve recipes. Please try again.');
		}
	},

	getAvailableRecipes: async (
		page: number,
		size: number,
		availableIngredientIds: string
	): Promise<PageResponseDTO<RecipeDetailsDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			params.append("ingredientIds", availableIngredientIds);

			const { data } = await api.get<PageResponseDTO<RecipeDetailsDTO>>(`${BASE_URL}/available?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch recipes:', error);
			throw new Error('Unable to retrieve recipes. Please try again.');
		}
	},

	createRecipe: async (payload: RecipeDTO): Promise<RecipeDetailsDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create recipe:', error);
			throw new Error('Unable to create recipe. Please try again.');
		}
	},

	updateRecipe: async (
		id: number,
		payload: RecipeDTO
	): Promise<RecipeDetailsDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update recipe with id ${id}:`, error);
			throw new Error(`Unable to update recipe with id ${id}:. Please try again.`);
		}
	},

	deleteRecipe: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete recipe with id ${id}:`, error);
			throw new Error(`Unable to delete recipe with id ${id}:. Please try again.`);
		}
	},

}