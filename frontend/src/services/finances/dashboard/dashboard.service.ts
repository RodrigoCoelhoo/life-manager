import { api } from '../../api';
import type { DashboardOverviewDTO } from './dashboard.dto';

const BASE_URL = '/dashboard';

export const dashboardService = {

	getMonthOverview: async (yearMonth: string): Promise<DashboardOverviewDTO> => {
		try {
			const { data } = await api.get(`${BASE_URL}/${yearMonth}`);
			return data;
		} catch(error) {
			console.error('Failed to fetch monthly finances overview:', error);
			throw new Error('Unable to fetch monthly finances overview. Please try again.');
		}
	},

}