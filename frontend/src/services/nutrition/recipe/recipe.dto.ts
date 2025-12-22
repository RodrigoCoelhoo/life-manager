import type { IngredientResponseDTO } from "../ingredient/ingredient.dto";

export interface RecipeIngredientResponseDTO {
	ingredient: IngredientResponseDTO;
	amount: number;
	unit: string;
}

export interface RecipeDetailsDTO {
	id: number;
	name: string;
	ingredients: RecipeIngredientResponseDTO[];	
}

export interface RecipeIngredientDTO {
	id: number;
	amount: number;
	unit: string;
}

export interface RecipeDTO {
	name: string;
	ingredients: RecipeIngredientDTO[];
}

export const Unit = {
	MG: "mg",
	G: "g",
	KG: "kg",
	ML: "ml",
	L: "l",
} as const;
export type Unit = keyof typeof Unit;