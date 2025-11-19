import type { WalletResponseDTO } from "../wallet/wallet.dto";

export const ExpenseType = {
	EXPENSE: "EXPENSE",
	INCOME: "INCOME",
} as const;
export type ExpenseType = typeof ExpenseType[keyof typeof ExpenseType];

export const ExpenseCategory = {
	SALARY: "SALARY",
	FREELANCE: "FREELANCE",
	SELL_INVESTMENT: "SELL_INVESTMENT",

	HOUSING: "HOUSING",
	FOOD: "FOOD",
	HEALTH: "HEALTH",
	ENTERTAINMENT: "ENTERTAINMENT",
	TRANSPORTATION: "TRANSPORTATION",
	EDUCATION: "EDUCATION",
	BUY_INVESTMENT: "BUY_INVESTMENT",
	OTHER: "OTHER"
} as const;
export type ExpenseCategory = typeof ExpenseCategory[keyof typeof ExpenseCategory];

export interface TransactionDTO {
	walletId: number;
	amount: string;
	description: string;
	date: string;
	category: ExpenseCategory;
};

export interface TransactionResponseDTO {
	id: number;
	wallet: WalletResponseDTO;
	amount: string;
	type: ExpenseType;
	description: string;
	date: string;
	category: ExpenseCategory;
};