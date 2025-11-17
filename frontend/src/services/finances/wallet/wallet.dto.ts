export interface WalletDTO {
	name: string;
	type: string;
	balance: string;
	currency: string;
}

export interface WalletResponseDTO {
	id: string;
	name: string;
	type: string;
	balance: string;
	currency: string;
}

export interface WalletUpdateDTO {
	name: string;
	type: string;
	currency: string;
}