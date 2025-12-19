import type { IngredientBrandDetailsResponseDTO, IngredientBrandResponseDTO } from "../ingredient-brand/ingredient-brand.dto";

export interface IngredientDTO {
	name: string;
}

export interface IngredientResponseDTO {
	id: number;
	name: string;
	brands: IngredientBrandResponseDTO[]
}

export interface IngredientDetailsDTO {
	id: number;
	name: string;
	brands: IngredientBrandDetailsResponseDTO[];
}