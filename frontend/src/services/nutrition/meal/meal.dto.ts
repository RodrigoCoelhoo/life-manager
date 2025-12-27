export interface MealDTO {
	date: string;
	ingredients: MealIngredientDTO[];
}

export interface MealIngredientDTO {
	ingredientId: number;
	brandId: number;
	amount: number;
	unit: string;
}

export interface MealResponseDTO {
	id: number;
	dateTime: string;
	ingredients: MealIngredientResponseDTO[];
}

export interface MealDetailsDTO {
	meal: MealResponseDTO;
	nutritionalLabel: NutritionalLabelEntrieDTO[];
}

export interface NutritionalLabelEntrieDTO {
	nutrient: string;
	amount: number;
	unit: string;
}

export interface MealIngredientResponseDTO {
	ingredientId: number;
	ingredient: string;
	brandId: number;
	brand: string;
	amount: number;
	unit: string;
}