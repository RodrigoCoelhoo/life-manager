export interface UserDTO {
	username: string;
	firstName: string;
	lastName: string;
	email: string;
	passwordConfirmation: string;
}

export interface UpdatePasswordDTO {
	newPassword: string;
	passwordConfirmation: string;
}

export interface UserResponseDTO {
	username: string;
	firstName: string;
	lastName: string;
	email: string;
}