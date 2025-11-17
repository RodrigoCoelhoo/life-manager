export interface DashboardOverviewDTO {
	yearMonth: string;
	totalIncome: string;
	incomeCategories: Record<string, string>;
	totalExpenses: string;
	expenseCategories: Record<string, string>;
	netBalance: string;
}