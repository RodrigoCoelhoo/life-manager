export interface SignInDTO {
	username: string;
	password: string;
}

export interface AccessTokenResponseDTO {
	accessToken: string;
}

export interface SignUpDTO {
	username: string;
	firstName: string;
	lastName: string;
	email: string;
	password: string;
}

export interface SignUpResponseDTO {
	id: number;
	username: string;
	firstName: string;
	lastName: string;
	email: string;
}