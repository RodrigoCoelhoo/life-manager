import type { WalletSimpleResponseDTO } from "../wallet/wallet.dto";

export interface TransferenceDTO {
	fromWalletId: number;
	toWalletId: number;
	amount: string;
	date: string;
	description: string;
}

export interface TransferenceResponseDTO {
	id: number;
	fromWallet: WalletSimpleResponseDTO
	fromAmount: string;
	toWallet: WalletSimpleResponseDTO
	toAmount: string;
	date: string;
	description: string;
}