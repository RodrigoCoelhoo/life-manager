import { api } from '../../api';
import type { MonthOverviewDTO } from './dashboard.dto';

const BASE_URL = '/dashboard/training';

export const dashboardService = {

	getMonthOverview: async (date: string): Promise<MonthOverviewDTO> => {
		try {
			const { data } = await api.get(`${BASE_URL}/month-overview/${date}`);
			return data;
		} catch(error) {
			console.error('Failed to fetch monthly training overview:', error);
			throw new Error('Unable to fetch monthly training overview. Please try again.');
		}
	},

}
