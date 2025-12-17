import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { AutomaticTransactionDTO, AutomaticTransactionResponseDTO } from './automatic-transactions.dto';

const BASE_URL = '/automatic-transactions';

export const automaticTransactionService = {

	getAutomaticTransactions: async (
		page: number,
		size: number
	): Promise<PageResponseDTO<AutomaticTransactionResponseDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			const { data } = await api.get<PageResponseDTO<AutomaticTransactionResponseDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch automatic transactions:', error);
			throw new Error('Unable to retrieve automatic transactions. Please try again.');
		}
	},

	createAutomaticTransaction: async (payload: AutomaticTransactionDTO): Promise<AutomaticTransactionResponseDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create automatic transaction:', error);
			throw new Error('Unable to create automatic transaction. Please try again.');
		}
	},

	updateAutomaticTransaction: async (
		id: number,
		payload: AutomaticTransactionDTO
	): Promise<AutomaticTransactionResponseDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update automatic transaction with id ${id}:`, error);
			throw new Error(`Unable to update automatic transaction with id ${id}:. Please try again.`);
		}
	},

	deleteAutomaticTransaction: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete automatic transaction with id ${id}:`, error);
			throw new Error(`Unable to delete automatic transaction with id ${id}:. Please try again.`);
		}
	},

	triggerAutomaticTransaction: async (id: number): Promise<void> => {
		try {
			await api.post(`${BASE_URL}/${id}/trigger`);
		} catch (error) {
			console.error(`Failed to trigger automatic transaction with id ${id}:`, error);
			throw new Error(
				`Unable to trigger automatic transaction with id ${id}. Please try again.`
			);
		}
	}
}