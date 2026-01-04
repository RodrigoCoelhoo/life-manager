import { api } from '../../api';
import type { WeekOverviewDTO } from './dashboard.dto';

const BASE_URL = '/dashboard/nutrition';

export const dashboardService = {

	getWeekOverview: async (date: string): Promise<WeekOverviewDTO> => {
		try {
			const { data } = await api.get(`${BASE_URL}/week-overview/${date}`);
			return data;
		} catch(error) {
			console.error('Failed to fetch weekly nutrition overview:', error);
			throw new Error('Unable to fetch weekly nutrition overview. Please try again.');
		}
	},

}