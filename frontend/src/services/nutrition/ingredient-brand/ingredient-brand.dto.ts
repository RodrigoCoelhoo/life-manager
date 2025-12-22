export interface IngredientBrandDTO {
	name: string;
	nutritionalValues: NutritionalValueDTO[];
}

export interface IngredientBrandResponseDTO {
	id: number;
	name: string;
}

export interface IngredientBrandDetailsResponseDTO {
	id: number;
	name: string;
	nutritionalValues: NutritionalValueResponseDTO[];
}

export interface NutritionalValueDTO {
	type: NutritionalTag;
	per100units: number;
}

export interface NutritionalValueResponseDTO {
	id: number;
	nutrient: NutritionalTag;
	per100units: number;
	unit: string;
}

export const NutritionalTag = {
	CALORIES: "CALORIES",

	PROTEIN: "PROTEIN",
	CARBOHYDRATES: "CARBOHYDRATES",
	SUGARS: "SUGARS",
	FIBER: "FIBER",
	FAT: "FAT",
	SATURATED_FAT: "SATURATED_FAT",
	TRANS_FAT: "TRANS_FAT",
	OMEGA_3: "OMEGA_3",
	CHOLESTEROL: "CHOLESTEROL",

	SODIUM: "SODIUM",
	POTASSIUM: "POTASSIUM",
	CALCIUM: "CALCIUM",
	IRON: "IRON",

	VITAMIN_A: "VITAMIN_A",
	VITAMIN_C: "VITAMIN_C",
	VITAMIN_D: "VITAMIN_D",
	VITAMIN_E: "VITAMIN_E",
	VITAMIN_K: "VITAMIN_K",
	VITAMIN_B12: "VITAMIN_B12",
} as const;
export type NutritionalTag = keyof typeof NutritionalTag;

export const NutritionalTagUnit: Record<NutritionalTag, string> = {
	CALORIES: "kcal",

	PROTEIN: "g",
	CARBOHYDRATES: "g",
	SUGARS: "g",
	FIBER: "g",
	FAT: "g",
	SATURATED_FAT: "g",
	TRANS_FAT: "g",
	OMEGA_3: "g",
	CHOLESTEROL: "mg",

	SODIUM: "mg",
	POTASSIUM: "mg",
	CALCIUM: "mg",
	IRON: "mg",

	VITAMIN_A: "µg",
	VITAMIN_C: "mg",
	VITAMIN_D: "µg",
	VITAMIN_E: "mg",
	VITAMIN_K: "µg",
	VITAMIN_B12: "µg",
};