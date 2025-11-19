export interface DashboardOverviewDTO {
  yearMonth: string;                     
  totalIncome: string;                   
  incomeCategories: CategorySummaryDTO[];
  totalExpenses: string;                 
  expenseCategories: CategorySummaryDTO[];
  netBalance: string;                    
}

export interface CategorySummaryDTO {
  category: string;  
  amount: string;    
}