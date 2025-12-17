import type { WalletResponseDTO } from "../wallet/wallet.dto";
import type { ComponentType, JSX, ReactElement } from "react";
import type { IconBaseProps } from "react-icons";

import {
	FaMoneyBillWave,
	FaCode,
	FaChartLine,
	FaCoins,
	FaHome,
	FaUtensils,
	FaHeartbeat,
	FaFilm,
	FaCar,
	FaGraduationCap,
	FaPiggyBank,
	FaQuestionCircle,
	FaDollarSign,
	FaShoppingCart,
	FaBriefcase
} from "react-icons/fa";
import { PiChartLineDown, PiChartLineDownBold } from "react-icons/pi";

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

export const CategoryColors: Record<ExpenseCategory, string> = {
	SALARY: "#4d7eb3",
	FREELANCE: "#6fb3d3",
	SELL_INVESTMENT: "#2dffff",
	PASSIVE_INCOME: "#ceffff",

	HOUSING: "#6f5dc1",
	FOOD: "#b38dd6",
	HEALTH: "#9b89d3",
	ENTERTAINMENT: "#a855f7",
	TRANSPORTATION: "#c4b7e5",
	EDUCATION: "#b36cc4",
	BUY_INVESTMENT: "#A599F0",
	OTHER: "#ffffff"
};

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

export const CategoryIconComponents: Record<ExpenseCategory, ComponentType<IconBaseProps>> = {
	SALARY: FaDollarSign,
	FREELANCE: FaBriefcase,
	SELL_INVESTMENT: PiChartLineDownBold,
	PASSIVE_INCOME: FaCoins,

	HOUSING: FaHome,
	FOOD: FaUtensils,
	HEALTH: FaHeartbeat,
	ENTERTAINMENT: FaFilm,
	TRANSPORTATION: FaCar,
	EDUCATION: FaGraduationCap,
	BUY_INVESTMENT: FaChartLine ,
	OTHER: FaQuestionCircle
};