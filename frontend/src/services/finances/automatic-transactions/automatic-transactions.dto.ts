export interface AutomaticTransactionDTO {
	walletId: string;
	amount: string;
	category: string;
	recurrence: string;
	interval: number;
	description: string;
	nextTransactionDate: string;
};

export interface AutomaticTransactionResponseDTO {
	id: string;
	walletName: string;
	amount: string;
	category: string;
	type: string;
	recurrence: string;
	interval: number;
	description: string;
	nextTransactionDate: string;
};