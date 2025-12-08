import type { ExpenseCategory } from "../transaction/transaction.dto";

export interface DashboardOverviewDTO {
  yearMonth: string;                     
  totalIncome: string;                   
  incomeCategories: CategorySummaryDTO[];
  totalExpenses: string;                 
  expenseCategories: CategorySummaryDTO[];
  netBalance: string;                    
}

export interface CategorySummaryDTO {
  category: ExpenseCategory;  
  amount: string;    
}