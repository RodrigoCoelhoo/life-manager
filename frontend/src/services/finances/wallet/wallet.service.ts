import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { WalletDTO, WalletResponseDTO, WalletUpdateDTO } from './wallet.dto';

const BASE_URL = '/wallets';

export const walletService = {

	getAllWallets: async (
		page: number,
		size: number,
		name: string
	): Promise<PageResponseDTO<WalletResponseDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			if (name && name.trim() !== "") {
				params.append("name", name);
			}

			const { data } = await api.get<PageResponseDTO<WalletResponseDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch wallets:', error);
			throw new Error('Unable to retrieve wallets. Please try again.');
		}
	},

	getWallet: async (id: number): Promise<WalletResponseDTO> => {
		try {
			const { data } = await api.get<WalletResponseDTO>(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to fetch wallet with id ${id}:`, error);
			throw new Error('Unable to retrieve wallet. Please try again.');
		}
	},

	createWallet: async (payload: WalletDTO): Promise<WalletResponseDTO> => {
		try {
			const { data } = await api.post<WalletResponseDTO>(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create wallet:', error);
			throw new Error('Unable to create wallet. Please try again.');
		}
	},

	updateWallet: async (
		id: number,
		payload: WalletUpdateDTO
	): Promise<WalletResponseDTO> => {
		try {
			const { data } = await api.put<WalletResponseDTO>(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update wallet with id ${id}:`, error);
			throw new Error('Unable to update wallet. Please try again.');
		}
	},

	deleteWallet: async (id: number): Promise<void> => {
		try {
			await api.delete(`${BASE_URL}/${id}`);
		} catch (error) {
			console.error(`Failed to delete wallet with id ${id}:`, error);
			throw new Error('Unable to delete wallet. Please try again.');
		}
	},
};
