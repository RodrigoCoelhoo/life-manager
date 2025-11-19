import type { ExpenseCategory, ExpenseType } from "../transaction/transaction.dto";

export interface AutomaticTransactionDTO {
	walletId: number;
	amount: string;
	category: ExpenseCategory;
	recurrence: string;
	interval: number;
	description: string;
	nextTransactionDate: string;
};

export interface AutomaticTransactionResponseDTO {
	id: number;
	walletName: string;
	amount: string;
	category: ExpenseCategory;
	type: ExpenseType;
	recurrence: string;
	interval: number;
	description: string;
	nextTransactionDate: string;
};