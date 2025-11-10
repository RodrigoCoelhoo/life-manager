import { api } from '../api';
import type {
	SignInDTO,
	SignInResponseDTO,
	SignUpDTO,
	SignUpResponseDTO
} from './authTypes';

export const authService = {

	signin: async (payload: SignInDTO): Promise<SignInResponseDTO> => {
		const { data } = await api.post<SignInResponseDTO>('/auth/signin', payload);
		return data;
	},

	signup: async (payload: SignUpDTO): Promise<SignUpResponseDTO> => {
		const { data } = await api.post<SignUpResponseDTO>('/auth/signup', payload);
		return data;
	},
};
