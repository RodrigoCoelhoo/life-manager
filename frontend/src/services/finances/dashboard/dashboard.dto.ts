import type { AutomaticTransactionSimple } from "../automatic-transactions/automatic-transactions.dto";
import type { ExpenseCategory, TransactionResponseDTO } from "../transaction/transaction.dto";
import type { TransferenceResponseDTO } from "../transference/transference.dto";
import type { WalletResponseDTO } from "../wallet/wallet.dto";

export interface MonthOverviewDTO {
	yearMonth: string;
	totalIncome: string;
	incomeCategories: CategorySummaryDTO[];
	totalExpenses: string;
	expenseCategories: CategorySummaryDTO[];
	netBalance: string;
	wallets: WalletResponseDTO[];
	recentTransactions: TransactionResponseDTO[];
	recentTransferences: TransferenceResponseDTO[];
	automaticTransactions: AutomaticTransactionSimple[];
	previousMonthsNetBalance: MonthNetBalanceResponseDTO[];
}

export interface CategorySummaryDTO {
	category: ExpenseCategory;
	amount: string;
}

export interface MonthNetBalanceResponseDTO {
	yearMonth: string;
	income: number;
	expenses: number;
	netBalance: number;
}