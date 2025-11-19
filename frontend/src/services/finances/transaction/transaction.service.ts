import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { TransactionDTO, TransactionResponseDTO } from './transaction.dto';

const BASE_URL = '/transactions';

export const transactionService = {

	getAllTransactions: async (): Promise<PageResponseDTO<TransactionResponseDTO>> => {
		try {
			const { data } = await api.get<PageResponseDTO<TransactionResponseDTO>>(BASE_URL);
			return data;
		} catch (error) {
			console.error('Failed to fetch transactions:', error);
			throw new Error('Unable to retrieve transactions. Please try again.');
		}
	},

	createTransaction: async (payload: TransactionDTO): Promise<TransactionResponseDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create transaction:', error);
			throw new Error('Unable to create transaction. Please try again.');
		}
	},

	updateTransaction: async (
		id: number,
		payload: TransactionDTO
	): Promise<TransactionResponseDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update transaction with id ${id}:`, error);
			throw new Error(`Unable to update transaction with id ${id}:. Please try again.`);
		}
	},

	deleteTransaction: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete transaction with id ${id}:`, error);
			throw new Error(`Unable to delete transaction with id ${id}:. Please try again.`);
		}
	},

}