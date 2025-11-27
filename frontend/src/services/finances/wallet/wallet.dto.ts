export const WalletType = {
  BANK: "BANK",
  CASH: "CASH",
} as const;
export type WalletType = (typeof WalletType)[keyof typeof WalletType];

export interface WalletDTO {
	name: string;
	type: WalletType;
	balance: string;
	currency: string;
}

export interface WalletResponseDTO {
	id: number;
	name: string;
	type: WalletType;
	balance: string;
	currency: string;
}

export interface WalletUpdateDTO {
	name: string;
	type: WalletType;
	currency: string;
}

export interface WalletSimpleResponseDTO {
	id: number;
	name: string;
}