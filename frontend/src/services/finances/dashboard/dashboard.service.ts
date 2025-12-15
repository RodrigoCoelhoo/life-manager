import { api } from '../../api';
import type { MonthOverviewDTO } from './dashboard.dto';

const BASE_URL = '/dashboard';

export const dashboardService = {

	getMonthOverview: async (yearMonth: string, currency: string): Promise<MonthOverviewDTO> => {
		try {
			const { data } = await api.get(`${BASE_URL}/month-overview/${yearMonth}/${currency}`);
			return data;
		} catch(error) {
			console.error('Failed to fetch monthly finances overview:', error);
			throw new Error('Unable to fetch monthly finances overview. Please try again.');
		}
	},

}