import type { WalletResponseDTO } from "../wallet/wallet.dto";

export interface TransactionDTO {
	walletId: string;
	amount: string;
	description: string;
	date: string;
	category: string;
};

export interface TransactionResponseDTO {
	id: string;
	wallet: WalletResponseDTO;
	amount: string;
	type: string;
	description: string;
	date: string;
	category: string;
};