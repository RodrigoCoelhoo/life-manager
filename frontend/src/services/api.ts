import axios from 'axios';
import { authService } from './auth/authService';

let accessToken: string | null = null;
let logoutFn: (() => void) | null = null;

export const registerLogout = (fn: () => void) => {
	logoutFn = fn;
};

export const setAccessToken = (token: string) => {
	accessToken = token;
};

export const clearAccessToken = () => {
	accessToken = null;
};

export const api = axios.create({
	baseURL: "http://localhost:8080/api",
	headers: { "Content-Type": "application/json" },
	withCredentials: true,
});

const PUBLIC_ENDPOINTS = ["/auth/signin", "/auth/signup"];

api.interceptors.request.use(config => {
	if (accessToken) {
		config.headers.Authorization = `Bearer ${accessToken}`;
	}
	return config;
});

api.interceptors.response.use(
	res => res,
	async err => {
		const original = err.config;

		if (PUBLIC_ENDPOINTS.some(ep => original.url?.includes(ep))) {
			return Promise.reject(err); 
		}

		if (original.url.includes("/auth/refresh")) {
			clearAccessToken();
			if (logoutFn) logoutFn();
			return Promise.reject(err);
		}

		if (err.response?.status === 401 && !original._retry) {
			original._retry = true;
			try {
				const newToken = await authService.refresh();
				setAccessToken(newToken);
				original.headers.Authorization = `Bearer ${newToken}`;
				return api(original);
			} catch (refreshErr) {
				clearAccessToken();
				if (logoutFn) logoutFn();
				return Promise.reject(refreshErr);
			}
		}

		return Promise.reject(err);
	}
);