import type { ExpenseCategory, ExpenseType } from "../transaction/transaction.dto";
import type { WalletSimpleResponseDTO } from "../wallet/wallet.dto";

export interface AutomaticTransactionDTO {
	walletId: number;
	name: string;
	amount: string;
	category: ExpenseCategory;
	recurrence: string;
	interval: number;
	description: string;
	nextTransactionDate: string;
};

export interface AutomaticTransactionResponseDTO {
	id: number;
	wallet: WalletSimpleResponseDTO;
	name: string;
	amount: string;
	category: ExpenseCategory;
	type: ExpenseType;
	recurrence: TransactionRecurrence;
	interval: number;
	description: string;
	nextTransactionDate: string;
};

export interface AutomaticTransactionSimple {
	id: number;
	walletName: string;
	name: string;
	amount: string;
	category: ExpenseCategory;
	type: ExpenseType;
	nextTransactionDate: string;
}

export const TransactionRecurrence = {
	DAILY: "DAILY",
	WEEKLY: "WEEKLY",
	MONTHLY: "MONTHLY",
	YEARLY: "YEARLY"
} as const;
export type TransactionRecurrence = keyof typeof TransactionRecurrence;