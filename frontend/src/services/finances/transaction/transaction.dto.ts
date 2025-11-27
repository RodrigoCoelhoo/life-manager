import type { WalletResponseDTO } from "../wallet/wallet.dto";

export const ExpenseType = {
	INCOME: "INCOME",
	EXPENSE: "EXPENSE"
} as const;
export type ExpenseType = keyof typeof ExpenseType;

export const ExpenseCategory = {
	SALARY: "SALARY",
	FREELANCE: "FREELANCE",
	SELL_INVESTMENT: "SELL_INVESTMENT",
	PASSIVE_INCOME: "PASSIVE_INCOME",

	HOUSING: "HOUSING",
	FOOD: "FOOD",
	HEALTH: "HEALTH",
	ENTERTAINMENT: "ENTERTAINMENT",
	TRANSPORTATION: "TRANSPORTATION",
	EDUCATION: "EDUCATION",
	BUY_INVESTMENT: "BUY_INVESTMENT",
	OTHER: "OTHER"
} as const;
export type ExpenseCategory = keyof typeof ExpenseCategory;

export const ExpenseCategoryType: Record<ExpenseCategory, ExpenseType> = {
	[ExpenseCategory.SALARY]: ExpenseType.INCOME,
	[ExpenseCategory.FREELANCE]: ExpenseType.INCOME,
	[ExpenseCategory.SELL_INVESTMENT]: ExpenseType.INCOME,
	[ExpenseCategory.PASSIVE_INCOME]: ExpenseType.INCOME,

	[ExpenseCategory.HOUSING]: ExpenseType.EXPENSE,
	[ExpenseCategory.FOOD]: ExpenseType.EXPENSE,
	[ExpenseCategory.HEALTH]: ExpenseType.EXPENSE,
	[ExpenseCategory.ENTERTAINMENT]: ExpenseType.EXPENSE,
	[ExpenseCategory.TRANSPORTATION]: ExpenseType.EXPENSE,
	[ExpenseCategory.EDUCATION]: ExpenseType.EXPENSE,
	[ExpenseCategory.BUY_INVESTMENT]: ExpenseType.EXPENSE,
	[ExpenseCategory.OTHER]: ExpenseType.EXPENSE
};

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