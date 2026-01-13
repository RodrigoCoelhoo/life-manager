import { api } from '../api';
import type { UpdatePasswordDTO, UserDTO, UserResponseDTO } from './user.dto';

const BASE_URL = '/users';

export const userService = {

	getProfile: async (): Promise<UserResponseDTO> => {
		try {
			const { data } = await api.get<UserResponseDTO>(`${BASE_URL}/profile`);
			return data;
		} catch (error) {
			console.error('Failed to fetch profile.', error);
			throw new Error('Unable to retrieve profile. Please try again.');
		}
	},

	updateProfile: async (payload: UserDTO): Promise<UserResponseDTO> => {
		try {
			const { data } = await api.put<UserResponseDTO>(`${BASE_URL}/update-profile`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update profile.`, error);
			throw new Error(`Unable to update profile. Please try again.`);
		}
	},

	updatePassword: async (
		payload: UpdatePasswordDTO
	): Promise<void> => {
		try {
			const { data } = await api.put(`${BASE_URL}/update-password`, payload);
			return data;
		} catch (error) {
			console.error(`Failed to update password.`, error);
			throw new Error(`Unable to update password. Please try again.`);
		}
	},
}