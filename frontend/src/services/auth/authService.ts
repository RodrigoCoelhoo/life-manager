import { api, setAccessToken, clearAccessToken } from '../api';
import type {
	SignInDTO,
	AccessTokenResponseDTO,
	SignUpDTO,
	SignUpResponseDTO
} from './authTypes';

export const authService = {
	signin: async (payload: SignInDTO): Promise<AccessTokenResponseDTO> => {
		try {
			const { data } = await api.post<AccessTokenResponseDTO>('/auth/signin', payload);
			setAccessToken(data.accessToken);
			return data;
		} catch (error) {
			clearAccessToken();
			throw error;
		}
	},

	signup: async (payload: SignUpDTO): Promise<SignUpResponseDTO> => {
		const { data } = await api.post<SignUpResponseDTO>('/auth/signup', payload);
		return data;
	},

	refresh: async (): Promise<string> => {
		try {
			const { data } = await api.post<{ accessToken: string }>('/auth/refresh');
			setAccessToken(data.accessToken);
			return data.accessToken;
		} catch (error) {
			clearAccessToken();
			throw error;
		}
	},
};