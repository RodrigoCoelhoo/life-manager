export interface TransferenceDTO {
	fromWalletId: string;
	toWalletId: string;
	amount: string;
	date: string;
	description: string;
}

export interface TransferenceResponseDTO {
	id: string;
	fromWalletName: string;
	fromAmount: string;
	toWalletName: string;
	toAmount: string;
	date: string;
	description: string;
}