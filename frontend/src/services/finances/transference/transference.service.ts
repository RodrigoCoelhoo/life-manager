import type { TransferenceFilters } from '../../../pages/finances/Transferences';
import { api } from '../../api';
import type { PageResponseDTO } from '../../api.dto';
import type { TransferenceDTO, TransferenceResponseDTO } from './transference.dto';

const BASE_URL = '/transferences';

export const transferenceService = {

	getAllTransferences: async (
		page: number, 
		size: number, 
		filters: TransferenceFilters
	): Promise<PageResponseDTO<TransferenceResponseDTO>> => {
		try {
			const params = new URLSearchParams({
				page: page.toString(),
				size: size.toString(),
			});

			if (filters.sender) params.append("senderId", filters.sender.id.toString());
			if (filters.receiver) params.append("receiverId", filters.receiver.id.toString());
			if (filters.startDate) params.append("startDate", filters.startDate);
			if (filters.endDate) params.append("endDate", filters.endDate);

			const { data } = await api.get<PageResponseDTO<TransferenceResponseDTO>>(`${BASE_URL}?${params.toString()}`);
			return data;
		} catch (error) {
			console.error('Failed to fetch transferences:', error);
			throw new Error('Unable to retrieve transferences. Please try again.');
		}
	},

	getTransference: async (id: number): Promise<TransferenceResponseDTO> => {
		try {
			const { data } = await api.get(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to fetch transference with ${id}:`, error);
			throw new Error(`Unable to retrieve transference with ${id}. Please try again.`);
		}
	},

	createTransference: async (payload: TransferenceDTO): Promise<TransferenceResponseDTO> => {
		try {
			const { data } = await api.post(BASE_URL, payload);
			return data;
		} catch (error) {
			console.error('Failed to create transference:', error);
			throw new Error('Unable to create transference. Please try again.');
		}
	},

	updateTransference: async (
		id: number,
		payload: TransferenceDTO
	): Promise<TransferenceResponseDTO> => {
		try {
			const { data } = await api.put(`${BASE_URL}/${id}`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update transference with id ${id}:`, error);
			throw new Error(`Unable to update transference with id ${id}:. Please try again.`);
		}
	},

	deleteTransference: async (id: number): Promise<void> => {
		try {
			const { data } = await api.delete(`${BASE_URL}/${id}`);
			return data;
		} catch (error) {
			console.error(`Failed to delete transference with id ${id}:`, error);
			throw new Error(`Unable to delete transference with id ${id}:. Please try again.`);
		}
	},
}