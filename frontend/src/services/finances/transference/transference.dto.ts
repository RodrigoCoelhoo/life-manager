export interface TransferenceDTO {
	fromWalletId: number;
	toWalletId: number;
	amount: string;
	date: string;
	description: string;
}

export interface TransferenceResponseDTO {
	id: number;
	fromWalletName: string;
	fromAmount: string;
	toWalletName: string;
	toAmount: string;
	date: string;
	description: string;
}